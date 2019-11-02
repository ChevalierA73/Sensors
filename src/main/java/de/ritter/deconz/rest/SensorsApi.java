package de.ritter.deconz.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.Future;

@Slf4j
@Component
public class SensorsApi {

    @Value("${raspberry.pi}")
    private String address;

    @Value("${raspberry.apikey}")
    private String apikey;

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.producer.topicName}")
    private String topicName;

    public KafkaProducer<String, String> kafkaProducer() throws UnknownHostException {

        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("bootstrap.servers", kafkaBootstrapServers);
        config.put("acks", "all");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<String, String>(config);

    }

    @Scheduled(fixedRate = 300000)
    public void getAllSensors() throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        final String uri = "http://"+address+"/api/"+apikey+"/sensors";

        String result = restTemplate.getForObject(uri, String.class);

        log.info("sensors response {}", result);

        sendKafkaMessage(result, kafkaProducer(), topicName);

    }

    private void sendKafkaMessage(String payload, KafkaProducer<String, String> producer, String topic)
    {
        final ProducerRecord<String, String> record = new ProducerRecord<>(topic, "sensors", payload);
        Future<RecordMetadata> future = producer.send(record);
    }

}
