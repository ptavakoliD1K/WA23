package com.welfenhub.services;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * sends email
 */

@Service
public class EmailSendingService {

    EmailSendingService() {
    }

    /**
     * sends prepared email
     *
     * @param myAccount
     * @param myPassword
     * @param file
     * @param pdfFileName
     * @throws MessagingException
     * @throws IOException
     */

    public static void sendEmail(String myAccount, String myPassword, File file, String pdfFileName) throws MessagingException, IOException {

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
        Message message = prepareMessageWithPdf(session, myAccount, "dennis.abel@swisslife.de", file, pdfFileName);
        Transport.send(message);
        System.out.println("E-Mail erfolgreich versendet an " + myAccount);
    }

    /**
     * prepares email with pdf
     *
     * @param session
     * @param myAccount
     * @param receiver
     * @param file
     * @param pdfFileName
     * @return message
     * @throws MessagingException
     * @throws IOException
     */

    private static Message prepareMessageWithPdf(Session session, String myAccount, String receiver, File file, String pdfFileName) throws MessagingException {

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(myAccount));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
        message.setSubject("Eine neue Dozentenevaluation");

        Multipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Guten Tag, \n \nanbei befindet sich eine neue Dozentenevaluation. \n\nFreundliche Grüße \nDein WelfenHub Team");
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(pdfFileName);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);

        return message;
    }
}
