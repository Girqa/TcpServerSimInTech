package com.example.tcpserver.grpc;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.smarteps.batterycontroller.grpc.DataSample;


import java.util.function.Consumer;

import static ru.smarteps.batterycontroller.grpc.MessageServiceGrpc.*;


@Slf4j
@Component
@Profile({"grpc"})
public class GrpcClientImpl implements GrpcClient {

    @Value("${grpc.server.address}")
    private String serverAddress;

    @Value("${grpc.server.port}")
    private int serverPort;

    private MessageServiceStub stub;

    @Override
    public void connect() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();
        stub = newStub(channel);

    }

    @Override
    public void sendData(DataSample dataSample) {
        log.info("Send sample: \n{}", dataSample);
        stub.sendDataSample(dataSample, new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty empty) {
            }
            @Override
            public void onError(Throwable throwable) {
            }
            @Override
            public void onCompleted() {
            }
        });
    }

    @Override
    public void subscribe(Consumer<DataSample> listener) {
        stub.openDataStream(Empty.newBuilder().build(), new StreamObserver<DataSample>() {
            @Override
            public void onNext(DataSample dataSample) {
                listener.accept(dataSample);
                log.info("Received: \n{}", dataSample);
            }
            @Override
            public void onError(Throwable throwable) {
            }
            @Override
            public void onCompleted() {
            }
        });
    }

    @PostConstruct
    private void init() {
        log.info("Connects to gRPC server");
        connect();
        log.info("Connected to gRPC server");
    }
}

