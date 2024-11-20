package com.WelfenHub.services;

import com.WelfenHub.models.User;
import com.WelfenHub.models.UserRole;
import com.WelfenHub.repositories.UserRepository;
import com.WelfenHub.repositories.RoleRepository;
import com.WelfenHub.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

import java.util.Collections;
import java.util.List;

/////////////////////////////

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


@Service
public class PasswordResetService {

    private static final String MY_ACCOUNT = "welfenhub@gmail.com";
    private static final String MY_PASSWORD = "bkpf bewr lvfh cskf"; // Google-App Passwort
    private static final String TEST_RECIPIENT = "simon.pollak@stud.welfenakademie.de";

    public String sendPasswordResetEmail(String empfaenger) {
        try {
            sendEmail(TEST_RECIPIENT);  // empfaenger
            return "E-Mail erfolgreich versendet!";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Fehler beim Senden der E-Mail.";
        }
    }

    private void sendEmail(String empfaenger) throws MessagingException {
        // E-Mail-Einstellungen konfigurieren
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MY_ACCOUNT, MY_PASSWORD);
            }
        });

        // E-Mail erstellen und senden
        Message message = prepareMessage(session, MY_ACCOUNT, empfaenger);
        Transport.send(message);
        System.out.println("E-Mail erfolgreich versendet an " + empfaenger);
    }

    private Message prepareMessage(Session session, String myAccount, String empfaenger) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(myAccount));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(empfaenger));
        message.setSubject("Passwort zurücksetzen");

        // Erstellen und Hinzufügen des Inhalts der E-Mail
        Multipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Hier klicken, um das Passwort zurückzusetzen.");
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);

        return message;
    }
}