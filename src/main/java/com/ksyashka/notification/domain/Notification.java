package com.ksyashka.notification.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Notification {
    private String title;
    private String commonInfo;
    private Map<String, String> attributes = new HashMap<>();

    public Notification(String title) {
        this.title = title;
    }

    public String getNotificationText() {
        StringBuilder text = new StringBuilder(this.getCommonInfo());
        Map<String, String> attributes = this.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            attributes.forEach((key, value) -> {
                text.append("\n");
                text.append(key);
                text.append(": ");
                text.append(value);
            });
        }
        return text.toString();
    }
}
