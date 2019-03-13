package de.ritter.deconz.api;

import lombok.Data;

import java.util.Date;

@Data
public class State {

    private String dark;

    private String daylight;

    private Integer status;

    private Integer humidity;

    private Integer pressure;

    private Integer temperature;

    private Date lastupdated;

    private Boolean lowbattery;

    private Boolean tampered;

    private Boolean water;

}
