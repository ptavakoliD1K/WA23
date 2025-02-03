package com.welfenhub.services;

import com.welfenhub.repositories.EvaluationFilesRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * sends evaluation by email
 */

@Service
public class SendEvaluationByEmailService {

    @Autowired
    EvaluationFilesRepository evaluationFilesRepository;

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
     * @param date date of creation of rating
     * @throws FileNotFoundException
     */

    public void sendEvaluationMail(String date) throws FileNotFoundException {

        LocalDateTime creationDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


        String creationDateVisible = formatter.format(creationDate).toString();
        String fileName = "Dozenten_Evaluation_" + creationDateVisible + ".pdf";

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(evaluationFilesRepository.getFileFromDatabase(date).get(0));

            File pdfRating = new File("Dozenten_Evaluation.pdf");

            try(FileOutputStream fileOutputStream = new FileOutputStream(pdfRating)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                EmailSendingService.sendEmail(myAccountStatic, myPasswordStatic, pdfRating, fileName);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
