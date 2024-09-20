package org.mlk.kjm.email;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.mlk.kjm.shared.ApplicationProperties;

public class EmailService {
    private final String from;

    public EmailService(ApplicationProperties appProps) {
        from = appProps.getMailFrom();
        String host = appProps.getMailHost();
        Properties properties = System.getProperties();
        String mailHost = "mail.smtp.host";
        // String mailUser = appProps.getMailUser();
        // String mailPassword = appProps.getMailPassword();
        properties.setProperty(mailHost, host);
        // properties.setProperty(mailUser, host);
        // properties.setProperty(mailPassword, host);
    }
    
    public void sendEmail(String to, String subject, String text) {    
        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
