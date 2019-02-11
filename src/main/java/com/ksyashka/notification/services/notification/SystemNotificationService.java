package com.ksyashka.notification.services.notification;

import com.ksyashka.notification.constants.NotificationWay;
import com.ksyashka.notification.domain.Client;
import com.ksyashka.notification.domain.Notification;
import com.notification.NotificationFactory;
import com.notification.manager.SlideManager;
import com.notification.types.TextNotification;
import com.theme.ThemePackagePresets;
import com.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SystemNotificationService implements NotificationServiceI {

    @Value("${system.notification.delay.sec}")
    private long notificationDelay;
    private NotificationFactory factory;
    private SlideManager notificationManager;
    private NotificationServiceCollector notificationServiceCollector;

    @Autowired
    public SystemNotificationService(NotificationServiceCollector notificationServiceCollector) {
        System.setProperty("java.awt.headless", "false");
        factory = new NotificationFactory(ThemePackagePresets.cleanLight());
        notificationManager = new SlideManager(NotificationFactory.Location.SOUTHEAST);
        this.notificationServiceCollector = notificationServiceCollector;
    }

    @PostConstruct
    private void init() {
        notificationServiceCollector.registerService(NotificationWay.SYSTEM, this);
    }

    @Override
    public void sendNotification(Notification notification, Client client) {
        notificationManager.setSlideDirection(SlideManager.SlideDirection.NORTH);
        TextNotification systemNotification = factory.buildTextNotification(notification.getTitle(), notification.getNotificationText());
        systemNotification.setCloseOnClick(true);
        notificationManager.addNotification(systemNotification, Time.seconds(notificationDelay));
    }
}
