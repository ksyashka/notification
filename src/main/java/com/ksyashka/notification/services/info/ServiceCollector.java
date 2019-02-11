package com.ksyashka.notification.services.info;

import com.ksyashka.notification.constants.NotificationServiceType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ServiceCollector {
    private Map<NotificationServiceType, GetNotificationInfoService> services = new HashMap<>();

    public void registerService(NotificationServiceType name, GetNotificationInfoService service) {
        services.put(name, service);
    }

    public GetNotificationInfoService get(NotificationServiceType name) {
        return services.get(name);
    }
}
