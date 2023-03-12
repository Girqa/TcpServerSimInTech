package com.example.tcpserver.tcp_client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


// TODO: develop like server

/**
 * DEPRECATED
 */
@Slf4j
//@Service
//@PropertySource(value = "classpath:application.yaml", factory = YamlPropertySourceFactory.class)
public class TcpClient implements Client{
    @Value("${tcp-server.port}")
    private int connectionPort;
    private Socket socket;
    private PrintWriter writer = null;
    private BufferedInputStream reader = null;

    @SneakyThrows
    @Override
    public void connect() {
        socket = new Socket("localhost", connectionPort);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedInputStream((socket.getInputStream()));
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ses.schedule(() -> {
            try {
                System.out.println(Arrays.toString(new int[]{reader.read()}));
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
            }, 100, TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    @Override
    public void disconnect() {
        socket.close();
    }

    @SneakyThrows
    @Override
    public void publish(String data) {
//        writer.println(data);
//        String response = reader.readLine();
//        log.info("Response is {}", response);
    }
}
