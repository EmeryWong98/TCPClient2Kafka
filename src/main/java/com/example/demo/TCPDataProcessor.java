package com.example.demo;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Component
public class TCPDataProcessor {

    private final String tcpServerHost;
    private final int tcpServerPort;
    private final String kafkaBootstrapServers;
    private final String kafkaTopic;

    @Autowired
    public TCPDataProcessor(AppConfig appConfig) {
        this.tcpServerHost = appConfig.getTcpServerHost();
        this.tcpServerPort = appConfig.getTcpServerPort();
        this.kafkaBootstrapServers = appConfig.getKafkaBootstrapServers();
        this.kafkaTopic = appConfig.getKafkaTopic();
    }

    public void processData() {
        // Kafka Producer Properties
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", kafkaBootstrapServers);
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try {
            // Create Kafka Producer
            KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(kafkaProps);

            // Create Kafka Admin Client
            Properties adminProps = new Properties();
            adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
            AdminClient adminClient = AdminClient.create(adminProps);

            // Create Kafka Topic if it does not exist
            if (!topicExists(adminClient, kafkaTopic)) {
                // createTopic()
                createTopic(adminClient, kafkaTopic);
            }

            // Connect to Remote TCP server
            Socket socket = new Socket(tcpServerHost, tcpServerPort);
            System.out.println("Successfully connected to remote TCP server: " + tcpServerHost + ":" + tcpServerPort);

            // Read data from TCP server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                // Send data to Kafka
                ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTopic, line);
                kafkaProducer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        System.err.println("Error occurs when sending message to Kafka topic: " + exception.getMessage());
                    } else {
                        System.out.println("Successfully sending message to Kafka topic" + metadata.topic() + ", partition: " + metadata.partition() + ", offset: " + metadata.offset());
                    }
                });

                // Refresh Kafka Producer
                kafkaProducer.flush();
            }

            // Close resources
            socket.close();
            kafkaProducer.close();
            adminClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean topicExists(AdminClient adminClient, String topic) {
        try {
            return adminClient.listTopics().names().get().contains(topic);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createTopic(AdminClient adminClient, String topic) {
        NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
        // Making clean rule for topic
//        newTopic.configs(Collections.singletonMap("retention.ms", String.valueOf(10000)));
        adminClient.createTopics(Collections.singletonList(newTopic));
        System.out.println("Kafka topic has been created successfully: " + topic);
    }
}