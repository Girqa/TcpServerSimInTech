package com.example.tcpserver.services;

import com.example.tcpserver.tcp_client.Client;
import com.example.tcpserver.tcp_server.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SimInTechSessionService implements SessionService{
    private final Server server;
    private final Client client;
    private Thread session;

    public SimInTechSessionService(Server server, Client client) {
        this.server = server;
        this.client = client;
    }
    @Override
    public void startSession() {
        server.start();
        server.subscribe(s -> log.info("Msg: {}", s));
    }

    @Override
    public void stopSession() {
        server.stop();
    }
}
