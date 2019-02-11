package com.ksyashka.notification.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URL;

public class JsonParser {

    private ObjectMapper mapperJson;

    public JsonParser() {
        mapperJson = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public JsonParser(ObjectMapper mapperJson) {
        this.mapperJson = mapperJson;
        mapperJson.registerModule(new JavaTimeModule());
    }

    public String prepareObjectJson(Object o) throws IOException {
        return mapperJson.writeValueAsString(o);
    }

    public String preparePrettyJson(Object o) throws IOException {
        return mapperJson.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }

    public <T> T parseObjectJson(String obj, Class<T> c) throws IOException {
        return mapperJson.readValue(obj, c);
    }

    public <T> T parseFile(URL resource, Class<T> clazz) throws IOException {
        return mapperJson.readValue(resource, clazz);
    }
}

