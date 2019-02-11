package com.ksyashka.notification.services.notification;

import com.ksyashka.notification.constants.NotificationWay;
import com.ksyashka.notification.domain.Client;
import com.ksyashka.notification.domain.Notification;
import com.ksyashka.notification.utils.SmtpSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Log4j2
public class EmailNotificationService implements NotificationServiceI {

    @Autowired
    private SmtpSender smtpSender;
    @Autowired
    private NotificationServiceCollector notificationServiceCollector;

    @PostConstruct
    private void init() {
        notificationServiceCollector.registerService(NotificationWay.EMAIL, this);
    }

    @Override
    public void sendNotification(Notification notification, Client client) {
        try {
            smtpSender.sendWithAuth(notification.getNotificationText(), client.getEmails(), notification.getTitle());
        } catch (Exception e) {
            log.warn("Error send notification" + notification + "via email for client " + client);
        }
    }
}
