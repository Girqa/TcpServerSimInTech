package com.example.tcpserver.tcp_server;

import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerData;
import com.example.tcpserver.services.ConverterService;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class TcpServer implements Server {
    private final ConverterService converter;
    private ServerSocket serverSocket;
    private List<Consumer<String>> listeners = new ArrayList<>();
    private ExecutorService es = Executors.newFixedThreadPool(2);
    private Future<?> task;
    @Value("${tcp-server.port}")
    private int port;
    @Value("${tcp-server.auto-start}")
    private boolean autoStart;

    public TcpServer(ConverterService converter) {
        this.converter = converter;
    }

    @SneakyThrows
    @Override
    public boolean start() {
        log.info("Starts server");
        serverSocket = new ServerSocket(port);

        es.submit(() -> {
            try {
                Socket clientSocket = serverSocket.accept();
                log.info("Connected client {}", clientSocket.getInetAddress());
                startServerCommunication(clientSocket);
            } catch (Exception e) {
                log.warn("Error connecting client occurred. Reason {}", e.getMessage());
            }
        });

        return true;
    }

    @Override
    public void stop() {
        try {
            if (!serverSocket.isClosed()) serverSocket.close();
            if (task != null) task.cancel(true);
            es.shutdown();
        } catch (Exception e) {
            log.warn("Can not stop server. Reason {}", e.getMessage());
        }

    }

    @Override
    public void subscribe(Consumer<String > consumer) {
        this.listeners.add(consumer);
    }

    private void startServerCommunication(Socket clientSocket) {
        try {
            BufferedOutputStream writer = new BufferedOutputStream(clientSocket.getOutputStream(), 20);
            BufferedInputStream reader = new BufferedInputStream(clientSocket.getInputStream(), 24);
            long modelTime = 0;
            long scheduleStep = 20;
            double timeStep = 0.02;
            byte[] buffer = new byte[24];
            AtomicInteger integer = new AtomicInteger(0);
            ServerData serverData = new ServerData(modelTime, new double[]{0});

            ScheduledExecutorService ses = Executors.newScheduledThreadPool(3);

            ses.scheduleWithFixedDelay(() -> {
                try {
                    while (reader.available() != integer.get() + 24) {}
                    integer.set(reader.available());
                    reader.read(buffer);
                    System.out.println(integer.get());
                    double time = serverData.getModelTime() + timeStep;
                    serverData.setModelTime(time);
                    serverData.getData()[0] = 5 * Math.sin(314 * time);
                    writer.write(converter.convertToBytes(serverData));
                    writer.flush();
                    log.info("Sent {}", serverData);
                } catch (Exception e) {
                    log.warn("Error: {}", e.getMessage());
                    e.printStackTrace();
                }
            }, scheduleStep, scheduleStep, TimeUnit.MILLISECONDS);
            ses.scheduleWithFixedDelay(() -> {
                try {
                    System.out.println(reader.available());
                    reader.read(buffer);
//                    ClientData clientData = converter.convertFromBytes(buffer);
//                    log.info("Received {}", clientData);
                } catch (IOException e) {
                    log.warn("Error: {}", e.getMessage());
                    e.printStackTrace();
                }
            }, scheduleStep, scheduleStep + 10, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("Bad communication. Reason {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @PostConstruct
    void init() {
        if (autoStart) {
            subscribe(d -> {log.info("Received {}", d);});
            start();
        }
    }
}
