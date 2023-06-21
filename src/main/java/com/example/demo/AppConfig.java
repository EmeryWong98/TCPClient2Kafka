package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${tcp.server.host}")
    private String tcpServerHost;

    @Value("${tcp.server.port}")
    private int tcpServerPort;

    @Value("${kafka.bootstrap.servers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    public String getTcpServerHost() {
        return tcpServerHost;
    }

    public void setTcpServerHost(String tcpServerHost) {
        this.tcpServerHost = tcpServerHost;
    }

    public int getTcpServerPort() {
        return tcpServerPort;
    }

    public void setTcpServerPort(int tcpServerPort) {
        this.tcpServerPort = tcpServerPort;
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }
}
