package com.example.tcpserver.services;

import com.example.tcpserver.configuration.DataProcessingCfg;
import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerData;
import com.example.tcpserver.model.buffers.Buffer;
import com.example.tcpserver.utils.Solver;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("furrier")
public class FurrierProcessingService extends DataProcessingService {
    protected int signalId;

    protected FurrierProcessingService(DataProcessingCfg cfg) {
        super(cfg);
    }

    @Override
    public void process(ClientData clientData, ServerData serverData) {
        signalId = (int) clientData.getData()[1];
        Buffer buffer = getFilledBuffer(clientData);

        if (buffer.available()) {
            double w = 2 * Math.PI * 50;
            double Fx = Solver.getFx(buffer.getArray(), w, dt * 2);
            double Fy = Solver.getFy(buffer.getArray(), w, dt * 2);

            if (Fx == 0) Fx = 10E-15;
            double A = Math.sqrt(Fx * Fx + Fy * Fy) / Math.sqrt(2);
            double fi = Math.atan(Fy / Fx) * 180 / Math.PI;
            serverData.getData()[signalId] = A;
            serverData.getData()[signalId + 2] = fi ;
        }

        double modelTime = clientData.getModelTime() + dt;
        serverData.setModelTime(modelTime);
    }
}
