package de.ritter.deconz.api;

import lombok.Data;

import java.util.Date;

@Data
public class State {

    private Integer humidity;

    private Integer pressure;

    private Integer temperature;

    private Date lastupdated;
}
