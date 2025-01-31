package com.welfenhub.services;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * sends evaluation by email
 */

@Service
public class SendEvaluationByEmailService {

    @Value("${spring.mail.username}")
    private String myAccount;

    @Value("${spring.mail.password}")
    private String myPassword;

    private static String myAccountStatic;

    private static String myPasswordStatic;

    /**
     * inserts non-static value into static
     */

    @PostConstruct
    private void init() {
        myAccountStatic = myAccount;
        myPasswordStatic = myPassword;
    }

    /**
     * starts sending process of email with pdf as input stream
     * @throws FileNotFoundException
     */

    public static void sendEvaluationMail() throws FileNotFoundException {
        try {
            // TODO Daten aus Datenbank abfragen
            InputStream inputStream = new FileInputStream("X:/git_repository/welfenhub_repo2/WA23/Project WelfenHub/pdfTest/test.pdf");

            EmailSendingService.sendEmail(myAccountStatic, myPasswordStatic, inputStream, "test.pdf");

            inputStream.close();
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }

    }

}
