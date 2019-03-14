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

        Map<String, Sensors> sensorMap = new HashMap<>();

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        for (Iterator<JsonNode> it = node.elements(); it.hasNext(); ) {

            Iterator<Map.Entry<String, JsonNode>> nodeMap = it.next().fields();

            Sensors sensor = getSensorByUniqueId(sensorMap, nodeMap);

            nodeMap.forEachRemaining(new Consumer<Map.Entry<String, JsonNode>>() {
                @Override
                public void accept(Map.Entry<String, JsonNode> stringJsonNodeEntry) {

                    String key = stringJsonNodeEntry.getKey();

                    try {

                        if ("config".equals(key)) {
                            sensor.setConfig(new ObjectMapper().readValue(stringJsonNodeEntry.getValue().toString(), Config.class));

                        } else if ("name".equals(key)) {

                            sensor.setName(stringJsonNodeEntry.getValue().toString());

                        } else if ("manufacturername".equals(key)) {

                            sensor.setManufacturername(stringJsonNodeEntry.getValue().toString());

                        } else if ("modelid".equals(key)) {

                            sensor.setModelid(stringJsonNodeEntry.getValue().toString());

                        } else if ("state".equals(key)) {

                            sensor.setState(new ObjectMapper().readValue(stringJsonNodeEntry.getValue().toString(), State.class));

                        }


                    } catch (IOException e) {
                        log.error("could not desirialize {} {}", node.toString(), e.getMessage());
                        e.printStackTrace();
                    }

                }
            });

            sensorMap.put(sensor.getUniqueid(), sensor);

        }

        return sensorMap.values();
    }

    private Sensors getSensorByUniqueId( Map<String, Sensors> sensorMap, Iterator<Map.Entry<String, JsonNode>> nodeMap) {

       final Sensors[] currentSensor = new Sensors[1];

        nodeMap.forEachRemaining(new Consumer<Map.Entry<String, JsonNode>>() {
            @Override
            public void accept(Map.Entry<String, JsonNode> stringJsonNodeEntry) {

                String key = stringJsonNodeEntry.getKey();

                if ("uniqueid".equals(key)) {

                    String uniqueid = stringJsonNodeEntry.getValue().toString();

                    currentSensor[0] = sensorMap.get(uniqueid);

                    if (null == currentSensor[0]) {
                        currentSensor[0] = new Sensors();
                        sensorMap.put(uniqueid, currentSensor[0])
                    }
                    currentSensor[0].setUniqueid(stringJsonNodeEntry.getValue().toString());

                }

            }

        });
        return currentSensor[0];
    }

}
