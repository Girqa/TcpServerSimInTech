package com.example.tcpserver.services;

import com.example.tcpserver.configuration.DataProcessingCfg;
import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerData;
import com.example.tcpserver.model.buffers.Buffer;
import com.example.tcpserver.model.buffers.CycleBuffer;

public abstract class DataProcessingService {
    protected double dt;
    protected Buffer[] buffers;
    protected int cycleInputs;
    protected int outputs;
    protected int bufferSize;

    protected DataProcessingService(DataProcessingCfg cfg) {
        dt = cfg.getTimeStep();
        cycleInputs = cfg.getCycleInputs();
        outputs = cfg.getOutputs();
        bufferSize = cfg.getBufferSize();
        switch (cfg.getBufferType()) {
            case "cycle" -> {
                buffers = new CycleBuffer[cycleInputs];
                for (int i = 0; i < cycleInputs; i++) {
                    buffers[i] = new CycleBuffer(bufferSize);
                }
            }
        }
    }

    public ServerData getInitData() {
        return new ServerData(0, new double[outputs]);
    }

    public abstract void process(ClientData clientData, ServerData serverData);

    public void clearBuffer() {
        for (Buffer buffer: buffers) {
            buffer.clear();
        }
    }

    protected Buffer getFilledBuffer(ClientData clientData) {
        int signalId = (int) clientData.getData()[1];
        Buffer buffer = buffers[signalId];
        buffer.put(clientData.getData()[0]);
        return buffer;
    }
}
