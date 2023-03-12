package com.example.tcpserver.services;

import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerData;

public interface DataProcessingService {
    ServerData getInitData();
    void process(ClientData clientData, ServerData serverData);
    void clearBuffer();
}
