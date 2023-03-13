package com.example.tcpserver.model.buffers;

public class CycleBuffer implements Buffer{
    private double[] buffer;
    private int bufLen;
    private int position;
    private int curCapacity;

    public CycleBuffer(int bufLen) {
        this.bufLen = bufLen;
        this.buffer = new double[bufLen];
        this.position = 0;
        this.curCapacity = 0;
    }

    @Override
    public boolean available() {
        return curCapacity == bufLen - 1;
    }

    @Override
    public void put(double val) {
        if (available()) {
            curCapacity = 0;
        } else {
            curCapacity++;
        }
        double right = buffer[bufLen - 1];
        buffer[bufLen - 1] = val;
        for (int i = bufLen - 2; i >= 0; i--) {
            double cur = buffer[i];
            buffer[i] = right;
            right = cur;
        }
    }

    @Override
    public double[] getArray() {
        return buffer;
    }

    @Override
    public void clear() {
        position = 0;
        curCapacity = 0;
        for (int i = 0; i < bufLen; i++) {
            buffer[i] = 0;
        }
    }
}
