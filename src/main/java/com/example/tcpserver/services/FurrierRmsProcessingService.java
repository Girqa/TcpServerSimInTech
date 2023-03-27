package com.example.tcpserver.services;

import com.example.tcpserver.configuration.DataProcessingCfg;
import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerData;
import com.example.tcpserver.model.buffers.Buffer;
import com.example.tcpserver.utils.Solver;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Profile("furrier-rms")
public class FurrierRmsProcessingService extends DataProcessingService {
    protected int signalId;
    protected double phaseLeakage;
    protected FurrierRmsProcessingService(DataProcessingCfg cfg) {
        super(cfg);
        phaseLeakage = 360 / cycleInputs / bufferSize / 2;
    }

    @Override
    public void process(ClientData clientData, ServerData serverData) {
        signalId = (int) clientData.getData()[1];
        Buffer buffer = getFilledBuffer(clientData);

        if (buffer.available()) {
            double rms = Solver.rms(buffer.getArray(), dt);

            double w = 2 * Math.PI * 50;

            double Fx = Solver.getFx(buffer.getArray(), w, dt * cycleInputs);
            double Fy = Solver.getFy(buffer.getArray(), w, dt * cycleInputs);
            double fi = Math.atan(Fy / Fx) * 180 / Math.PI - phaseLeakage * signalId;
            double A = Math.sqrt(Fx * Fx + Fy * Fy) / Math.sqrt(2);


            serverData.getData()[signalId] = rms;
            serverData.getData()[signalId + 2] = A;
            serverData.getData()[signalId + 4] = fi ;
        }
        double modelTime = clientData.getModelTime() + dt;
        serverData.setModelTime(modelTime);
    }
}

//    double Fx = Solver.getCorrectedFx(buffer.getArray(), w, dt * cycleInputs);
//    double Fy = Solver.getCorrectedFy(buffer.getArray(), w, dt * cycleInputs);
//    double fi = Math.atan(Fy / Fx) * 180 / Math.PI;
