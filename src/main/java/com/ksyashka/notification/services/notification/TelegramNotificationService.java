package com.ksyashka.notification.services.notification;

import com.ksyashka.notification.constants.NotificationWay;
import com.ksyashka.notification.domain.Client;
import com.ksyashka.notification.domain.HttpAnswer;
import com.ksyashka.notification.domain.Notification;
import com.ksyashka.notification.domain.TelegramMessage;
import com.ksyashka.notification.utils.HttpSender;
import com.ksyashka.notification.utils.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Log4j2
public class TelegramNotificationService implements NotificationServiceI {

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot%s/sendMessage";
    private static final String TITLE_SYMBOL = "📢 ";
    @Value("${telegram.token}")
    private String telegramToken;
    @Autowired
    private JsonParser jsonParser;
    @Autowired
    private HttpSender httpSender;
    @Autowired
    private NotificationServiceCollector notificationServiceCollector;

    @PostConstruct
    private void init() {
        notificationServiceCollector.registerService(NotificationWay.TELEGRAM, this);
    }

    @Override
    public void sendNotification(Notification notification, Client client) {
        TelegramMessage telegramMessage = TelegramMessage.builder()
                .chatId(client.getTelegramId()).text(TITLE_SYMBOL + notification.getTitle() + "\n" + notification.getNotificationText()).build();
        try {
            String jsonMessage = jsonParser.prepareObjectJson(telegramMessage);
            String url = String.format(TELEGRAM_API_URL, telegramToken);
            HttpAnswer answer = httpSender.post(url, jsonMessage);
            log.info("Telegram message {} was sent, answer {}", jsonMessage, answer);
        } catch (Exception e) {
            log.warn("Could not send telegram message {}. {}", telegramMessage, e.getMessage());
        }
    }
}

