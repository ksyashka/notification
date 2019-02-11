package com.ksyashka.notification.services.info;

import com.ksyashka.notification.domain.Notification;
import com.ksyashka.notification.domain.NotificationSettings;

public interface GetNotificationInfoService {

    Notification getNotificationInfo(NotificationSettings notificationSettings);
}
