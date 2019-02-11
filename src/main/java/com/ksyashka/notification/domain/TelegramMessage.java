package com.ksyashka.notification.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelegramMessage {
    @JsonProperty("chat_id")
    private String chatId;
    private String text;
}
