package com.example.tcpserver.services;

import com.example.tcpserver.configuration.DataProcessingCfg;
import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("smart-inverter")
public class SmartInverterProcessingService extends DataProcessingService{

    private double setPoint = 218.8;
    private double minDeltaU = 0.5;
    private double Integral = 0;
    private double P;
    private double Q;

    protected SmartInverterProcessingService(DataProcessingCfg cfg) {
        super(cfg);
    }

    @Override
    public void informOfClientDisconnection() {
        Integral = 0;
        P = 0;
        Q = 0;
    }

    @Override
    public void process(ClientData clientData, ServerData serverData) {
        double uMeas = clientData.getData()[0];
        double phaseMeas = clientData.getData()[1];
        log.info("U = {}∠{}.", uMeas, Math.toDegrees(phaseMeas));
        if (uMeas != 0) {
            double dU = calcEps(uMeas);
            if (dU != 0) {
                // 0.4 -> Ki
                Integral -= 0.7 * dU;
                // 5 -> maxOutputPower
                if (Integral >= 100) Integral = 100;
                else if (Integral <= 0) Integral = 0;
                log.info("Regulates U. dU={}. Integral = {}", dU, Integral);
            }
            P = Integral * Math.cos(phaseMeas);
            Q = Integral * Math.sin(phaseMeas);
        }

        serverData.setModelTime(clientData.getModelTime() + dt);
        serverData.setData(new double[]{P, Q});
    }

    private double calcEps(double uMeas) {
        if (uMeas > setPoint) {
            return uMeas - setPoint;
        } else if (uMeas < setPoint - minDeltaU) {
            return uMeas - setPoint + minDeltaU;
        } else {
            return 0;
        }
    }
}
