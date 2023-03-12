package com.example.tcpserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerData {
    private double modelTime = 0;
    private double[] data;
}
