package com.ksyashka.notification.services;

import com.ksyashka.notification.constants.NotificationServiceType;
import com.ksyashka.notification.domain.Client;
import com.ksyashka.notification.domain.Notification;
import com.ksyashka.notification.domain.NotificationSettings;
import com.ksyashka.notification.services.info.GetNotificationInfoService;
import com.ksyashka.notification.services.info.ServiceCollector;
import com.ksyashka.notification.services.notification.NotificationServiceCollector;
import com.ksyashka.notification.services.notification.NotificationServiceI;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class CommonNotificationService {

    @Autowired
    private ServiceCollector serviceCollector;
    @Autowired
    private NotificationServiceCollector notificationServiceCollector;
    private Map<String, Notification> clientNotification = new HashMap<>();

    public void handleClientNotification(Client client) {
        client.getSettings().forEach(s -> handleNotificationSettingsForClient(s, client));

    }

    private void handleNotificationSettingsForClient(NotificationSettings settings, Client client) {
        NotificationServiceType serviceName = settings.getService();
        GetNotificationInfoService service = serviceCollector.get(serviceName);
        if (service == null) {
            log.warn("Not found service: " + serviceName);
            return;
        }
        Notification notificationInfo = service.getNotificationInfo(settings);
        String key = client.getId() + settings.getId();
        log.info("Get " + serviceName + "notification");
        Notification notification = clientNotification.get(key);
        if (notification != null && notification.equals(notificationInfo)) {
            log.info("Notification was not changed");
            return;
        }
        clientNotification.put(key, notificationInfo);
        settings.getWays().forEach(way -> {
            NotificationServiceI notificationService = notificationServiceCollector.get(way);
            if (notificationService == null) {
                log.warn("Not found notification service: " + way);
                return;
            }
            log.info("Send info by" + way);
            notificationService.sendNotification(notificationInfo, client);
        });
    }
}
