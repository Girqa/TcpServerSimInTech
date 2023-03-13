package com.example.tcpserver.tcp_server;

import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.CommandID;
import com.example.tcpserver.configuration.ServerCfg;
import com.example.tcpserver.model.ServerData;
import com.example.tcpserver.services.ConverterService;
import com.example.tcpserver.services.DataProcessingService;
import com.example.tcpserver.services.RmsProcessingService;
import com.example.tcpserver.utils.YamlPropertySourceFactory;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
                              @Qualifier("furrier-rms") DataProcessingService processingService,
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
                        log.info("Model step begin");
                        // Receiving model data
                        reader.read(buffer);
                        ClientData clientData = converter.convertFromBytes(buffer);
                        log.info("Received {}", clientData);
                        // Input data processing
                        processingService.process(clientData, serverData);
                        // Sending model data
                        writer.write(converter.convertToBytes(serverData));
                        writer.flush();
                        log.info("Sent {}", serverData);
                        log.info("Model step end");
                        System.out.println();
                        if (clientData.getCommandID().equals(CommandID.DISCONNECT)) {
                            clientSocket.close();
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
