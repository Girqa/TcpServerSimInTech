package com.example.tcpserver.tcp_server;

import java.util.function.Consumer;

public interface Server {
    boolean start();
    void stop();
    void subscribe(Consumer<String> consumer);
}
