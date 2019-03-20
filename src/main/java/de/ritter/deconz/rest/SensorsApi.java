package de.ritter.deconz.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.ritter.deconz.api.Sensors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SensorsApi {

    @Value("${raspberry.pi}")
    private String address;

    @Value("${raspberry.apikey}")
    private String apikey;


    @Scheduled(fixedRate = 300000)
    public void getAllSensors() throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        final String uri = "http://"+address+"/api/"+apikey+"/sensors";

        String result = restTemplate.getForObject(uri, String.class);

        log.info("sensors response {}", result);

        Collection<Sensors> sensorsList = (Collection<Sensors>) new ObjectMapper().readValue(result, Sensors.class);

        log.info("received sensors number {} details {}", sensorsList.size(), sensorsList);

        log.info("senor names {}", sensorsList.stream().map(s -> s.getName()).collect(Collectors.toList()));

    }


}
