package com.example.tcpserver.tcp_server;

import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.CommandID;
import com.example.tcpserver.configuration.ServerCfg;
import com.example.tcpserver.model.ServerData;
import com.example.tcpserver.services.ConverterService;
import com.example.tcpserver.services.DataProcessingService;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Slf4j
@Service
public class TcpSimInTechServer implements Server {
    private final ConverterService converter;
    private final DataProcessingService processingService;
    private final ServerCfg config;
    private ServerSocket serverSocket;
    private ExecutorService es = Executors.newSingleThreadExecutor();
    private Future<?> clientsAccepting;
    private ScheduledFuture<?> clientConversation;

    public TcpSimInTechServer(ConverterService converter,
                              DataProcessingService processingService,
                              ServerCfg config) {
        this.converter = converter;
        this.processingService = processingService;
        this.config = config;
    }

    @SneakyThrows
    @Override
    public boolean start() {
        log.info("Starts server");
        serverSocket = new ServerSocket(config.getPort());

        clientsAccepting = es.submit(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    log.info("Connected client {}", clientSocket.getInetAddress());
                    startServerCommunication(clientSocket);
                } catch (Exception e) {
                    log.warn("Error connecting client occurred. Reason {}", e.getMessage());
                }
            }
        });

        return true;
    }

    @Override
    public void stop() {
        try {
            if (!serverSocket.isClosed()) serverSocket.close();
            if (clientConversation != null) clientConversation.cancel(true);
            if (clientsAccepting != null) clientsAccepting.cancel(true);
            es.shutdown();
            log.info("Server stopped successfully.");
        } catch (Exception e) {
            log.warn("Can not stop server. Reason {}", e.getMessage());
        }

    }

    private void startServerCommunication(Socket clientSocket) {
        try {
            BufferedOutputStream writer = new BufferedOutputStream(clientSocket.getOutputStream(), 44);
            BufferedInputStream reader = new BufferedInputStream(clientSocket.getInputStream(), config.getInputBufferSize());
            long scheduleStep = config.getScheduleStep();
            byte[] buffer = new byte[config.getInputBufferSize()];
            ServerData serverData = processingService.getInitData();
            processingService.clearBuffer();
            ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

            clientConversation = ses.scheduleWithFixedDelay(() -> {
                try {
                    if (clientSocket.isConnected()) {
                        log.trace("Model step begin");

                        // Receiving model data
                        reader.read(buffer);
                        ClientData clientData = converter.convertFromBytes(buffer);
                        log.trace("Received {}", clientData);

                        // Input data processing
                        processingService.process(clientData, serverData);

                        // Sending model data
                        writer.write(converter.convertToBytes(serverData));
                        writer.flush();
                        log.trace("Sent {}", serverData);
                        log.trace("Model step end");

                        if (clientData.getCommandID().equals(CommandID.DISCONNECT)) {
                            clientSocket.close();
                            processingService.informOfClientDisconnection();
                            log.info("Disconnects client");
                            clientConversation.cancel(true);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error: {}", e.getMessage());
                    e.printStackTrace();
                }
            }, scheduleStep, scheduleStep, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("Bad communication. Reason {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @PostConstruct
    void init() {
        if (config.isAutoStart()) {
            start();
        }
    }
}
