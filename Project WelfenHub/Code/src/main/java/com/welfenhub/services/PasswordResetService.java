package com.welfenhub.services;

import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 *  starts email-sending-process, creates e-mail and sends it for password reset
 */

@Service
public class PasswordResetService {

    @Value("${spring.mail.username}")
    private String myAccount; // Google-Account e-mail

    @Value("${spring.mail.password}")
    private String myPassword; // Google-Account password

    @Autowired
    private DataSource dataSource;

    /**
     * checks if email is in database and starts sending process
     * @param receiver
     * @throws SQLException
     */

    public void sendPasswordResetEmail(String receiver) throws SQLException {

        int result = 0;

        try(Connection conn = dataSource.getConnection()) {
            String insertSQL = "SELECT COUNT(*) FROM users WHERE email = ?";
            try(PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, receiver);
                try(ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        result = rs.getInt(1);
                    }
                }
            }
        }

        if (result != 1) {
            System.out.println("E-mail is not in the database");
            return;
        } else {
            try {
                sendEmail(receiver);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sends mail to receiver with information of e-mail client of Google
     * @param receiver
     * @throws MessagingException
     */

    private void sendEmail(String receiver) throws MessagingException, SQLException {
        // configure e-mail options
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
        Message message = prepareMessage(session, myAccount, receiver);
        Transport.send(message);
        System.out.println("E-Mail erfolgreich versendet an " + receiver);
    }

    /**
     * Creates message and content of e-mail, which is sent
     * @param session
     * @param myAccount
     * @param receiver
     * @return email
     * @throws MessagingException
     */

    private Message prepareMessage(Session session, String myAccount, String receiver) throws MessagingException, SQLException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(myAccount));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
        message.setSubject("Passwort zurücksetzen");

        // Erstellen und Hinzufügen des Inhalts der E-Mail
        Multipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Mit dem folgenden Link können Sie Ihr Passwort zurücksetzen: \n" + PasswordResetLinkService.linkGeneratorAndSaver(receiver));
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);

        return message;
    }
}
