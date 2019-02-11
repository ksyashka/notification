package com.ksyashka.notification.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ksyashka.notification.constants.CommonConstants.MILLIS_IN_SECOND;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpAnswer {

    private Integer code;
    private String text;
    private long duration;

    public String durationInSec() {
        return String.format("%.3fs", (double) duration / MILLIS_IN_SECOND);
    }

}


