package de.ritter.deconz.api;

import lombok.Data;

@Data
public class SensorType {

   private Integer ZHASwitch;

   private Integer ZHALight;

   private Boolean ZHAPresence;
}
