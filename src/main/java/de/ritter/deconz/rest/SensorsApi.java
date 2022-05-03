package de.ritter.deconz.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ritter.deconz.api.Sensors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Iterator;

@Slf4j
@Component
public class SensorsApi {
    @Value("${raspberry.pi}")
    private String address;

    @Value("${raspberry.apikey}")
    private String apikey;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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

            kafkaTemplate.send("sensors", sensorStr);

            log.info("Sensor {} ", sensors);
        }

        log.info("sensors response {}", nodes);

    }

}
