package de.ritter.deconz.api;

import lombok.Data;

import java.util.List;

@Data
public class Config {
    private Integer battery;
    private Integer offset;
    private Boolean on;
    private Boolean reachable;

    private String alert;

    private String group;
    private Boolean configured;
    private Boolean sunriseoffset;
    private Boolean sunsetoffset;
    private Integer temperature;
    private List<String> pending;

    private String duration;
}
