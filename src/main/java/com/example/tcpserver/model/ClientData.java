package com.example.tcpserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientData {
    private CommandID commandID;
    private double modelTime;
    private int inBufLen;
    private double[] data;
}
