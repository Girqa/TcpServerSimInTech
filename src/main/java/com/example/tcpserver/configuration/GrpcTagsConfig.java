package com.example.tcpserver.configuration;

import com.example.tcpserver.utils.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Data
@Configuration
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties("client")
public class GrpcTagsConfig {
    private List<String> meas;
    private List<String> ctrls;
}
