package com.example.tcpserver.services;

import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerCfg;
import com.example.tcpserver.model.ServerData;
import org.springframework.stereotype.Service;

import java.nio.DoubleBuffer;

@Service
public class RmsProcessingService implements DataProcessingService{
    private double dt;
    private DoubleBuffer buffer = DoubleBuffer.allocate(20);

    public RmsProcessingService(ServerCfg config) {
        dt = config.getTimeStep();
    }

    @Override
    public void process(ClientData clientData, ServerData serverData) {
        if (buffer.position() == 19) {
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
        serverData.getData()[0] = rms;
    }
}
