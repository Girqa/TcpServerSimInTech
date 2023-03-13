package com.example.tcpserver.model.buffers;

public interface Buffer {
    boolean available();
    void put(double val);
    double[] getArray();
    void clear();
}
