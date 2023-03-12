package com.example.tcpserver.services;

import com.example.tcpserver.configuration.DataProcessingCfg;
import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerData;
import org.springframework.stereotype.Service;

import java.nio.DoubleBuffer;

@Service
public class RmsProcessingService implements DataProcessingService{
    private double dt;
    private DoubleBuffer[] buffers;
    private int cycleInputs;
    private int bufferSize;

    public RmsProcessingService(DataProcessingCfg cfg) {
        dt = cfg.getTimeStep();
        cycleInputs = cfg.getCycleInputs();
        bufferSize = cfg.getBufferSize();
        buffers = new DoubleBuffer[cycleInputs];
        for (int i = 0; i < cycleInputs; i++) {
            buffers[i] = DoubleBuffer.allocate(bufferSize);
        }
    }

    @Override
    public ServerData getInitData() {
        return new ServerData(0, new double[cycleInputs]);
    }

    @Override
    public void process(ClientData clientData, ServerData serverData) {
        int signalId = (int) clientData.getData()[1];
        DoubleBuffer buffer = buffers[signalId];
        if (buffer.position() == bufferSize - 1) {
            buffer.put(clientData.getData()[0]);
            buffer.rewind();
        } else {
            buffer.put(clientData.getData()[0]);
        }
        double rms = 0;
        for (double v: buffer.array()) {
            rms += v*v;
        }
        rms /= 20;
        rms = Math.sqrt(rms);
        serverData.getData()[signalId] = rms;
        double modelTime = serverData.getModelTime() + dt;
        serverData.setModelTime(modelTime);
    }

    @Override
    public void clearBuffer() {
        for (DoubleBuffer buffer: buffers) {
            buffer.clear().put(new double[bufferSize]).clear();
        }
    }
}
