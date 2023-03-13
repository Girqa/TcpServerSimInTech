package com.example.tcpserver.services;

import com.example.tcpserver.configuration.DataProcessingCfg;
import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerData;
import com.example.tcpserver.model.buffers.Buffer;
import org.springframework.stereotype.Service;

@Service("rms")
public class RmsProcessingService extends DataProcessingService{
    public RmsProcessingService(DataProcessingCfg cfg) {
        super(cfg);
    }

    @Override
    public void process(ClientData clientData, ServerData serverData) {
        int signalId = (int) clientData.getData()[1];
        Buffer buffer = getFilledBuffer(clientData);
        if (buffer.available()) {
            double rms = 0;
            for (double v: buffer.getArray()) {
                rms += v*v;
            }
            rms /= bufferSize;
            rms = Math.sqrt(rms);
            serverData.getData()[signalId] = rms;
        }
        double modelTime = clientData.getModelTime() + dt;
        serverData.setModelTime(modelTime);
    }
}
