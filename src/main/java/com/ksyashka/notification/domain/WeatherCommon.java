package com.ksyashka.notification.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WeatherCommon {
    private Integer temp;
    private String city;
    private String errorString;

    public WeatherCommon(String city) {
        this.city = city;
    }
}
