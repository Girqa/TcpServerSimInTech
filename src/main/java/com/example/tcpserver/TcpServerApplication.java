package com.example.tcpserver;

import com.example.tcpserver.services.SessionService;
import com.example.tcpserver.services.SessionServiceCmd;
import com.example.tcpserver.services.SimInTechSessionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TcpServerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TcpServerApplication.class, args);

        SessionService service = context.getBean(SimInTechSessionService.class);
        service.startSession();
    }



























}
