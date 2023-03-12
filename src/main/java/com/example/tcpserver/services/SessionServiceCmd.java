package com.example.tcpserver.services;

import com.example.tcpserver.tcp_client.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Slf4j
@Service
public class SessionServiceCmd implements SessionService {
    private final Client client;
    private Thread session;

    public SessionServiceCmd(Client client) {
        this.client = client;
    }

    @Override
    public void startSession() {
        Scanner scanner = new Scanner(System.in);
        client.connect();
        log.info("Client is connected");
        session = new Thread(() -> {
            while (true) {
                log.info("Enter message:");
                String data = scanner.nextLine();
                client.publish(data);
            }
        });
        session.start();
    }

    @Override
    public void stopSession() {
        if (session != null) session.interrupt();
    }
}
