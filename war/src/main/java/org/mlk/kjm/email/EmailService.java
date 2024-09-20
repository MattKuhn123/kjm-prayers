package org.mlk.kjm.email;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.mlk.kjm.shared.ApplicationProperties;

public class EmailService {

    private final String address = "";
    private final String password = "";
    public EmailService(ApplicationProperties appProps) {
    }
    
    public void sendEmail(String to, String subject, String text) {
        Properties properies = new Properties();
        properies.put("mail.smtp.host", "smtp.gmail.com");
        properies.put("mail.smtp.port", "465");
        properies.put("mail.smtp.auth", "true");
        properies.put("mail.smtp.starttls.enable", "true");
        properies.put("mail.smtp.starttls.required", "true");
        properies.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properies.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properies,
          new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(address, password);
            }
          });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(address));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            System.out.println("Done");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
