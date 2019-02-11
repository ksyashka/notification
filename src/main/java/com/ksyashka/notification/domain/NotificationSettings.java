package com.ksyashka.notification.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ksyashka.notification.constants.NotificationServiceType;
import com.ksyashka.notification.constants.NotificationWay;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationSettings {
    private String id;
    private NotificationServiceType service;
    private List<NotificationWay> ways;
    private Map<String, String> attributes = new HashMap<>();
}
