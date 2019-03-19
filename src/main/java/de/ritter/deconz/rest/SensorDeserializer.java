package de.ritter.deconz.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.ritter.deconz.api.Config;
import de.ritter.deconz.api.Sensors;
import de.ritter.deconz.api.State;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class SensorDeserializer extends StdDeserializer<Collection<Sensors>> {

    public SensorDeserializer() {
        this(null);
    }

    protected SensorDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Collection<Sensors> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

       final Map<String, Sensors> sensorMap = new HashMap<>();

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        for (Iterator<JsonNode> it = node.elements(); it.hasNext(); ) {

            JsonNode jsonNode = it.next();

            String name = jsonNode.get("name").asText();

            if (it.hasNext()) {

                Iterator<Map.Entry<String, JsonNode>> nodeMap = it.next().fields();

                nodeMap.forEachRemaining(new Consumer<Map.Entry<String, JsonNode>>() {
                    @Override
                    public void accept(Map.Entry<String, JsonNode> stringJsonNodeEntry) {

                        Sensors sensors = sensorMap.get(name);

                        if (null == sensors) {
                            sensors = new Sensors();
                            sensors.setName(name);
                            sensorMap.put(name, sensors);
                        }

                        String key = stringJsonNodeEntry.getKey();

                        try {

                            if ("config".equals(key)) {
                                sensors.setConfig(new ObjectMapper().readValue(stringJsonNodeEntry.getValue().toString(), Config.class));

                            } else if ("manufacturername".equals(key)) {

                                sensors.setManufacturername(stringJsonNodeEntry.getValue().toString());

                            } else if ("modelid".equals(key)) {

                                sensors.setModelid(stringJsonNodeEntry.getValue().toString());

                            } else if ("state".equals(key)) {

                                sensors.setState(new ObjectMapper().readValue(stringJsonNodeEntry.getValue().toString(), State.class));

                            } else if ("etag".equals(key)) {

                                sensors.setEtag(stringJsonNodeEntry.getValue().toString());

                            }


                        } catch (IOException e) {
                            log.error("could not desirialize {} {}", node.toString(), e.getMessage());
                            e.printStackTrace();
                        }

                    }
                });

            }
        }

        return sensorMap.values();
    }

}
