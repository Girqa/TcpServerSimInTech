package com.example.tcpserver.configuration;

import com.example.tcpserver.utils.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties("processing")
public class DataProcessingCfg {
    private double timeStep;
    private int cycleInputs;
    private int outputs;
    private String bufferType;
    private int bufferSize;
}
