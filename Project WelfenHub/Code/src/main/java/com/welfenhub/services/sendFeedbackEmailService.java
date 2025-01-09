package com.welfenhub.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Properties;

@Service
public class sendFeedbackEmailService {

    public static void sendEmail(String senderEmail, String name, String feedbackMessage, String myAccount, String myPassword) throws MessagingException, SQLException {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccount, myPassword);
            }
        });

        // create and send email
        Message message = prepareMessage(session, myAccount, senderEmail, feedbackMessage, name, myAccount);
        Transport.send(message);
        System.out.println("E-Mail erfolgreich versendet an " + myAccount);
    }

    private static Message prepareMessage(Session session, String myAccount, String senderEmail, String feedbackMessage, String name, String receiver) throws MessagingException, SQLException {

        System.out.println("Test");

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(myAccount));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
        message.setSubject("Eine neue Feedback-Nachricht von " + name + ", E-Mail: " + senderEmail);

        // Erstellen und Hinzufügen des Inhalts der E-Mail
        Multipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(feedbackMessage);
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);

        return message;
    }
}
