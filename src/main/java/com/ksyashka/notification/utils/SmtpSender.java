package com.ksyashka.notification.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

import static com.ksyashka.notification.constants.SmtpConstants.*;

@Service
@Log4j2
public class SmtpSender {
    @Value("${smtp.host}")
    private String smtpHost;
    @Value("${smtp.port}")
    private String smtpPort;
    @Value("${smtp.support.username}")
    private String smtpUserName;
    @Value("${smtp.support.password}")
    private String smtpPassword;
    @Value("${smtp.support.from}")
    private String smtpFrom;
    @Value("${smtp.support.sender.name}")
    private String smtpSenderName;


    @Async
    public void sendWithAuth(String text, String to, String subject) throws IOException, MessagingException {
        Properties props = new Properties();
        props.put(SMTP_HOST, smtpHost);
        props.put(SMTP_PORT, smtpPort);
        props.put(SMTP_USER, smtpUserName);
        props.put(SMTP_AUTH_ENABLE, "true");
        props.put(SMTP_TLS_ENABLE, "true");

        Session session =
                Session.getInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(smtpUserName, smtpPassword);
                            }
                        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(smtpFrom));
        message.setSubject(subject);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setText(text);

        Transport.send(message);
    }

}

