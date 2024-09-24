package org.mlk.kjm.auth;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.mlk.kjm.shared.ApplicationProperties;
import org.mlk.kjm.users.AuthToken;

public class AuthCodeSender {

    private final String address;
    private final String password;
    public AuthCodeSender(ApplicationProperties appProps) {
        address = appProps.getEmailAddress();
        password = appProps.getEmailPassword();
    }
    
    public void sendAuthCode(AuthToken authToken) {
        String subject = "KJM Prayer Board code";
        String text = authToken.getCode().toString();
        String to = authToken.getEmail();

        Properties properies = new Properties();
        String[] host = { "mail.smtp.host", "smtp-mail.outlook.com" };
        properies.put(host[0], host[1]);
        
        String[] port = { "mail.smtp.port", "587" };
        properies.put(port[0], port[1]);

        String[] auth = { "mail.smtp.auth", "true" };
        properies.put(auth[0], auth[1]);

        String[] starttls = { "mail.smtp.starttls.enable", "true" };
        properies.put(starttls[0], starttls[1]);

        String[] protocols = {"mail.smtp.ssl.protocols", "TLSv1.2"};
        properies.put(protocols[0], protocols[1]);

        Session session = Session.getInstance(properies,
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(address, password);
                }
            }
        );

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(address));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
