package com.example.tcpserver.configuration;

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
    private int port = 19000;
    private boolean autoStart = true;
    private long scheduleStep = 1;
    private int inputBufferSize = 8;
}
