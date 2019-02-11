package com.ksyashka.notification.services.info;

import com.ksyashka.notification.domain.Currency;
import com.ksyashka.notification.domain.HttpAnswer;
import com.ksyashka.notification.domain.Notification;
import com.ksyashka.notification.domain.NotificationSettings;
import com.ksyashka.notification.exception.CannotGetInfo;
import com.ksyashka.notification.utils.HttpSender;
import com.ksyashka.notification.utils.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static com.ksyashka.notification.constants.CommonConstants.DEFAULT_TTL_HTTP_SENDER;
import static com.ksyashka.notification.constants.NotificationServiceType.CURRENCY;

@Service
@Log4j2
public class CurrencyService implements GetNotificationInfoService {
    @Value("${currency.url}")
    private String currencyUrl;
    @Autowired
    private ServiceCollector serviceCollector;
    @Autowired
    private HttpSender httpSender;
    @Autowired
    private JsonParser jsonParser;

    @PostConstruct
    private void init() {
        serviceCollector.registerService(CURRENCY, this);
    }

    @Override
    public Notification getNotificationInfo(NotificationSettings notificationSettings) {

        String currency = notificationSettings.getAttributes().get("currency");
        Notification notification = new Notification("Currency");
        HashMap<String, String> attributes = new HashMap<>();
        if (currency != null) {
            String[] currencies = currency.split(",");
            for (String c : currencies) {
                Optional<Currency> result = getCurrency(c);
                result.ifPresent(r -> attributes.put(r.getName(), Double.toString(r.getRate())));
            }
            if (!attributes.isEmpty()) {
                notification.setCommonInfo(LocalDate.now().toString());
                notification.setAttributes(attributes);
            } else {
                notification.setCommonInfo("Cannot get currency info");
            }
            return notification;
        }
        throw new CannotGetInfo("Can not get currency info");
    }

    public Optional<Currency> getCurrency(String currencyCode) {
        String currencyUrlWithParams = String.format(currencyUrl, currencyCode);
        HttpAnswer httpAnswer = httpSender.get(currencyUrlWithParams, DEFAULT_TTL_HTTP_SENDER);
        log.info("Currency request sent " + currencyUrlWithParams + " with answer: " + httpAnswer);
        try {
            Currency currency = jsonParser.parseObjectJson(httpAnswer.getText().replace("[","").replace("]",""), Currency.class);
            return Optional.of(currency);
        } catch (IOException e) {
            log.warn("Error parse currency info: " + e.getMessage());
        }
        return Optional.empty();
    }
}
