package com.ksyashka.notification.scheduler;

import com.ksyashka.notification.config.NotificationConfig;
import com.ksyashka.notification.services.CommonNotificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class Scheduler {

    @Autowired
    private NotificationConfig notificationConfig;
    @Autowired
    private CommonNotificationService commonNotificationService;

    @Scheduled(fixedDelayString = "${scheduled.milisec}")
    public void callScheduler() {
        log.info("Notification scheduler started working");
        log.info(notificationConfig.getClients());
        notificationConfig.getClients().forEach(
                client -> commonNotificationService.handleClientNotification(client));
    }
}
