package com.ksyashka.notification.utils;

import com.ksyashka.notification.config.NotificationConfig;
import com.ksyashka.notification.exception.ConfigFileException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static java.util.Optional.empty;

@Component
@Log4j2
public class ConfigLoader {

    @Autowired
    private JsonParser parser;
    private String configName="clients.json";


    public ConfigLoader(JsonParser parser) {
        this.parser = parser;
    }


    public NotificationConfig getNotificationConfig(String configPath) {
        Optional<URL> notificationConfigResource = loadResource(configPath  + File.separator + configName);
        if (notificationConfigResource.isPresent()) {
            try {
                log.info("Load notification config");
                return parser.parseFile(notificationConfigResource.get(), NotificationConfig.class);
            } catch (IOException e) {
                log.error("Could not load config " + e.getMessage(), e);
            }
        }
        throw new ConfigFileException("Could not load notification config");
    }


    private Optional<URL> loadResource(String configPath) {
        try {
            File file = new File(configPath);
            URL resource = file.toURI().toURL();
            return Optional.of(resource);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return empty();
    }

/*    public void updateConfig(String configPath, BoxConfiguration boxConfiguration, String configName) {
        File file = new File(configPath + File.separator + configName);
        try (FileWriter fw = new FileWriter(configPath + File.separator + configName)) {
            if (!file.exists() || !file.canWrite()) {
                log.warn("Could not update config file! ");
                throw new ConfigFileUpdateException("Could not update config file!");

            }

            String configJson;

            if (configName.equals(boxConfig)) {
                configJson = parser.preparePrettyJson(boxConfiguration.getBoxConfig());
            } else {
                configJson = parser.preparePrettyJson(boxConfiguration.getActionsConfig());
            }

            synchronized (this) {
                fw.write(configJson);
            }

        } catch (IOException e) {
            log.error("Updating config file error " + e.getMessage(), e);
        }
    }*/

}

