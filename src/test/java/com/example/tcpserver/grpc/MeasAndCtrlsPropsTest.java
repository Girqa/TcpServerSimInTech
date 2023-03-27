package com.example.tcpserver.grpc;

import com.example.tcpserver.services.GrpcProcessingService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;

@ActiveProfiles({"grpc"})
@SpringBootTest
public class MeasAndCtrlsPropsTest {

    @Autowired
    private GrpcProcessingService service;

    @SneakyThrows
    @Test
    public void checkMeasAndCtrls() {
        Field meas = service.getClass().getDeclaredField("meas");
        meas.setAccessible(true);
        Assertions.assertTrue(meas.get(service) != null);

        Field ctrls = service.getClass().getDeclaredField("ctrls");
        ctrls.setAccessible(true);
        Assertions.assertTrue(ctrls.get(service) != null);
    }
}
