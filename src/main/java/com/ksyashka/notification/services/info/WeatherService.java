package com.ksyashka.notification.services.info;

import com.ksyashka.notification.domain.*;
import com.ksyashka.notification.exception.CannotGetInfo;
import com.ksyashka.notification.utils.HttpSender;
import com.ksyashka.notification.utils.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.ksyashka.notification.constants.CommonConstants.DEFAULT_TTL_HTTP_SENDER;
import static com.ksyashka.notification.constants.NotificationServiceType.WEATHER;

@Service
@Log4j2
public class WeatherService implements GetNotificationInfoService {
    @Value("${weather.url}")
    private String weatherUrl;
    @Value("${weather.appid}")
    private String weatherToken;
    @Autowired
    private HttpSender httpSender;
    @Autowired
    private JsonParser jsonParser;
    @Autowired
    private ServiceCollector serviceCollector;

    @PostConstruct
    private void init() {
        serviceCollector.registerService(WEATHER, this);
    }


    private Optional<Weather> getWeatherInfo(String city) {
        String weatherUrlWithParams = String.format(weatherUrl, city, weatherToken);
        HttpAnswer httpAnswer = httpSender.get(weatherUrlWithParams, DEFAULT_TTL_HTTP_SENDER);
        log.info("Weather request sent " + weatherUrlWithParams + " with answer: " + httpAnswer);
        try {
            WeatherInfo weatherInfo = jsonParser.parseObjectJson(httpAnswer.getText(), WeatherInfo.class);
            return Optional.of(weatherInfo.getWeather());
        } catch (IOException e) {
            log.warn("Error parse weather info: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Notification getNotificationInfo(NotificationSettings notificationSettings) {
        String city = notificationSettings.getAttributes().get("city");
        if (city != null) {
            Notification notification = new Notification(city);
            Optional<Weather> weatherInfo = getWeatherInfo(city);
            if (!weatherInfo.isPresent()) {
                notification.setCommonInfo("Can not get weather info for city: " + city);
            } else {
                Weather weather = weatherInfo.get();
                int temp = weather.getTemp().intValue() - 273;
                notification.setCommonInfo("Temp " + Integer.toString(temp));
                Map<String, String> attributes = new HashMap<>();
                attributes.put("Humidity", weather.getHumidity().toString());
                attributes.put("Pressure", weather.getPressure().toString());
                notification.setAttributes(attributes);
            }
            return notification;
        }
        throw new CannotGetInfo("Can not get weather info");
    }
}
