package com.ksyashka.notification.config;

import com.ksyashka.notification.utils.ConfigLoader;
import com.ksyashka.notification.utils.HttpSender;
import com.ksyashka.notification.utils.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


@Configuration
@ComponentScan("com.ksyashka")
@EnableScheduling
@EnableAsync
public class AppConfig {

    @Autowired
    private ConfigLoader configLoader;
    @Value("${notification.pool.size.scheduler}")
    private int notificationPoolSizeScheduler;
    @Value("${config.path}")
    private String configPath;

    @Bean
    public NotificationConfig getNotificationConfig() {
        return configLoader.getNotificationConfig(configPath);
    }

    @Bean
    public JsonParser parser() {
        return new JsonParser();
    }

    @Bean
    public HttpSender getHttpSender() {
        return new HttpSender();
    }


    @Bean("notification-scheduler")
    public ThreadPoolTaskScheduler getScheduledExecutor() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(notificationPoolSizeScheduler);
        executor.setThreadNamePrefix("notification-scheduler");
        executor.initialize();
        return executor;
    }


}
