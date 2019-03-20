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
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

import org.apache.commons.beanutils.BeanUtilsBean;

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

       final Hashtable<String, Sensors> sensorTable = new Hashtable<>();

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        for (Iterator<JsonNode> it = node.elements(); it.hasNext(); ) {

            if (it.hasNext()) {

                JsonNode  jsonNode = it.next();
                final String[] etag = new String[1];

                if (null != jsonNode.get("etag")) {
                    etag[0] = jsonNode.get("etag").asText();
                }

                Iterator<Map.Entry<String, JsonNode>> nodeMap = jsonNode.fields();

                nodeMap.forEachRemaining(new Consumer<Map.Entry<String, JsonNode>>() {
                    @Override
                    public void accept(Map.Entry<String, JsonNode> stringJsonNodeEntry) {

                        Sensors sensors = sensorTable.get(etag[0]);

                        if (null == sensors) {
                            sensors = new Sensors();
                            sensorTable.put(etag[0], sensors);
                        }

                        String key = stringJsonNodeEntry.getKey();

                        try {

                            if ("name".equals(key)) {

                                sensors.setName(stringJsonNodeEntry.getValue().toString());

                            } else if ("config".equals(key)) {

                                Config newConfig = new ObjectMapper().readValue(stringJsonNodeEntry.getValue().toString(), Config.class);

                                if (null != sensors.getConfig()) {

                                    nullAwareBeanCopy(sensors.getConfig(), newConfig);

                                } else {

                                    sensors.setConfig(newConfig);

                                }

                            } else if ("manufacturername".equals(key)) {

                                sensors.setManufacturername(stringJsonNodeEntry.getValue().toString());

                            } else if ("modelid".equals(key)) {

                                sensors.setModelid(stringJsonNodeEntry.getValue().toString());

                            } else if ("state".equals(key)) {

                                State newState = new ObjectMapper().readValue(stringJsonNodeEntry.getValue().toString(), State.class);

                                if (null != sensors.getState()) {

                                    nullAwareBeanCopy(sensors.getState(), newState);

                                } else {

                                    sensors.setState(newState);

                                }

                            } else if ("etag".equals(key)) {

                                sensors.setEtag(stringJsonNodeEntry.getValue().toString());

                            } else if ("uniqueid".equals(key)) {

                                sensors.setUniqueid(stringJsonNodeEntry.getValue().toString());

                            }

                        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
                            log.error("could not desirialize {} {}", node.toString(), e.getMessage());
                            e.printStackTrace();
                        }

                    }
                });

            }
        }

        return sensorTable.values();
    }



    public static void nullAwareBeanCopy(Object dest, Object source) throws IllegalAccessException, InvocationTargetException {
        new BeanUtilsBean() {
            @Override
            public void copyProperty(Object dest, String name, Object value)
                    throws IllegalAccessException, InvocationTargetException {
                if(value != null) {
                    super.copyProperty(dest, name, value);
                }
            }
        }.copyProperties(dest, source);
    }

}
