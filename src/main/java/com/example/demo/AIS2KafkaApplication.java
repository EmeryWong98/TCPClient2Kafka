package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AIS2KafkaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AIS2KafkaApplication.class, args);
        TCPDataProcessor tcpDataProcessor = context.getBean(TCPDataProcessor.class);
        tcpDataProcessor.processData();
    }
}
