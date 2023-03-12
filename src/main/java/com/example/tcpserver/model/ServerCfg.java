package com.example.tcpserver.model;

import com.example.tcpserver.utils.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties("tcp-server")
public class ServerCfg {
    private int port;
    private boolean autoStart;
    private long scheduleStep = 1;
    private double timeStep = 0.001;
}
