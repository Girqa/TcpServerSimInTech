package com.example.tcpserver.services;

import com.example.tcpserver.configuration.DataProcessingCfg;
import com.example.tcpserver.configuration.GrpcTagsConfig;
import com.example.tcpserver.grpc.GrpcClient;
import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.ServerData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.smarteps.batterycontroller.grpc.DataSample;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
@Profile("grpc")
public class GrpcProcessingService extends DataProcessingService {

    private List<String> meas;

    private List<String> ctrls;

    private final GrpcClient grpcClient;

    private Map<String, Double> grpcServerCtrls;


    protected GrpcProcessingService(DataProcessingCfg cfg, GrpcClient grpcClient, GrpcTagsConfig grpcTagsConfig) {
        super(cfg);
        this.grpcClient = grpcClient;
        meas = grpcTagsConfig.getMeas();
        ctrls = grpcTagsConfig.getCtrls();
        grpcServerCtrls = new ConcurrentHashMap<>();
        ctrls.forEach(ctrl -> grpcServerCtrls.put(ctrl, 0d));
        grpcClient.subscribe(d -> grpcServerCtrls.put(d.getTag(), d.getValue()));
    }

    @SneakyThrows
    @Override
    public void process(ClientData clientData, ServerData tcpServerData) {
        double[] receivedData = clientData.getData();

        if (clientData.getModelTime() > 2) {

            for (int i = 0; i < receivedData.length; i++) {
                grpcClient.sendData(DataSample.newBuilder()
                        .setTag(meas.get(i))
                        .setValue(receivedData[i])
                        .build());
            }

            for (int i = 0; i < ctrls.size(); i++) {
                tcpServerData.getData()[i] = grpcServerCtrls.get(ctrls.get(i));
            }
        }

        tcpServerData.setModelTime(clientData.getModelTime() + dt);
    }
}
