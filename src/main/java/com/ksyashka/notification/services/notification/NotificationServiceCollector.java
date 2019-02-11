package com.ksyashka.notification.services.notification;

import com.ksyashka.notification.constants.NotificationWay;
import com.ksyashka.notification.services.notification.NotificationServiceI;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationServiceCollector {
    private Map<NotificationWay, NotificationServiceI> services = new HashMap<>();

    public void registerService(NotificationWay name, NotificationServiceI service) {
        services.put(name, service);
    }

    public NotificationServiceI get(NotificationWay name) {
        return services.get(name);
    }
}
