package com.example.tcpserver.grpc;

import ru.smarteps.batterycontroller.grpc.DataSample;

import java.util.function.Consumer;

public interface GrpcClient {

    void connect();

    void sendData(DataSample dataSample);

    void subscribe(Consumer<DataSample> listener);
}
