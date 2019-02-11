package com.ksyashka.notification;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
@Log4j2
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .listeners(new ApplicationPidFileWriter("notification.pid"))
                .run(args);
    }

    @PostConstruct
    public void init() {
        log.info("NOTIFICATION STARTED");

    }

    @PreDestroy
    public void destroy() {
        log.info("NOTIFICATION STOPPED");
    }
}
