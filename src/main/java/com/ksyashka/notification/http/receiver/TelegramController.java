package com.ksyashka.notification.http.receiver;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("telegram")
@Log4j2
public class TelegramController {

    @RequestMapping(value = "/webhook", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public void messageReceiver(@RequestBody String message) {
        log.info("Received a message {}", message);
    }
}
