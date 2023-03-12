package com.example.tcpserver.tcp_client;

public interface Client {
    void connect();
    void disconnect();
    void publish(String data);
}
