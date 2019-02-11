package com.ksyashka.notification.services.info;

import com.ksyashka.notification.domain.Notification;
import com.ksyashka.notification.domain.NotificationSettings;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.ksyashka.notification.constants.NotificationServiceType.SHOP;

@Service
@Log4j2
public class ShopService implements GetNotificationInfoService {

    @Autowired
    private ServiceCollector serviceCollector;

    @PostConstruct
    private void init() {
        serviceCollector.registerService(SHOP, this);
    }


    @Override
    public Notification getNotificationInfo(NotificationSettings notificationSettings) {
        return null;
    }
}
