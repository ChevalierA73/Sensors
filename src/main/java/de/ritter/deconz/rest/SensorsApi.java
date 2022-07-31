package de.ritter.deconz.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ritter.deconz.api.Sensors;
import de.ritter.deconz.repository.SensorRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Iterator;

@Slf4j
@Component
public class SensorsApi {
    public static final String SENSORS = "sensors";
    @Value("${raspberry.pi}")
    private String address;

    @Value("${raspberry.apikey}")
    private String apikey;

    @Value("${openweathermap.apikey}")
    private String weatherMapApiKey;

    @Autowired
    private SensorRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Scheduled(fixedRate = 1800000)
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

            mongoTemplate.insert(sensors);

            //Sensors storesSensors = repository.save(sensors);

            log.info("Sensor {} ", sensors);
        }

        log.info("sensors response {}", nodes);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?id=2810834&appid=" + weatherMapApiKey + "&units=metric")
                .get()
                .build();

        Response weaterMapApiresponse = client.newCall(request).execute();

        String weatherResponse = weaterMapApiresponse.body().string();

        log.info("current weather {}", weatherResponse);

    }

}
