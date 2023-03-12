package com.example.tcpserver.services;

import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerData;

public interface ConverterService {
    byte[] convertToBytes(ServerData serverData);
    ClientData convertFromBytes(byte[] bytes);
}
