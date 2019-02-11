package com.ksyashka.notification.services.notification;

import com.ksyashka.notification.domain.Client;
import com.ksyashka.notification.domain.Notification;

public interface NotificationServiceI {
    void sendNotification(Notification notification, Client client);
}
