package de.ritter.deconz.api;

import lombok.Data;

@Data
public class Config {

    //
    private Integer battery;

    private Integer offset;

    private Boolean on;

    private Boolean reachable;

}
