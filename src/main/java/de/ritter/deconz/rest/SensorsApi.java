package de.ritter.deconz.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ritter.deconz.api.Sensors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Component
public class SensorsApi {
    @Value("${raspberry.pi}")
    private String address;

    @Value("${raspberry.apikey}")
    private String apikey;

    @Bean
    public ProducerFactory<String, Sensors> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory(config);
    }

    @Bean
    public KafkaTemplate<String, Sensors> kafkaTemplate() {
        return new KafkaTemplate<String, Sensors>(producerFactory());
    }

    @Scheduled(fixedRate = 300000)
    public void getAllSensors() throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        final String uri = "http://" + address + "/api/" + apikey + "/sensors";

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(response.getBody());

        Iterator<JsonNode> nodes = root.elements();

        while (nodes.hasNext()) {

            String sensorStr = nodes.next().toString();

            ObjectMapper sensorsMapper = new ObjectMapper();

            Sensors sensors = sensorsMapper.readValue(sensorStr, Sensors.class);

            kafkaTemplate().send("sensors", sensors);

            log.info("Sensor {} ", sensors);
        }

        log.info("sensors response {}", nodes);

    }

}
