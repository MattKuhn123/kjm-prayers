package org.mlk.kjm.email;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.mlk.kjm.shared.ApplicationProperties;

public class EmailService {

    private final String address;
    private final String password;
    public EmailService(ApplicationProperties appProps) {
        address = appProps.getEmailAddress();
        password = appProps.getEmailPassword();
    }
    
    public void sendEmail(String to, String subject, String text) {
        Properties properies = new Properties();
        properies.put("mail.smtp.host", "smtp-mail.outlook.com");
        properies.put("mail.smtp.port", "587");
        properies.put("mail.smtp.auth", "true");
        properies.put("mail.smtp.starttls.enable", "true");
        properies.put("mail.smtp.ssl.protocols", "TLSv1.2");

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
            String msg = "sent code to " + to;
            System.out.println(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
