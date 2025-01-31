package com.welfenhub.controllers;

import com.welfenhub.services.SendFeedbackEmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

/**
 * empfängt Daten aus front-end und leitet sie weiter
 */

@Controller
public class FeedbackController {

    @Value("${spring.mail.username}")
    private String myAccount; // Google-Account e-mail

    @Value("${spring.mail.password}")
    private String myPassword; // Google-Account password


    /**
     * Empfängt Feedback Nachricht und Metadaten aus Frontend und verwertet sie weiter und ruft sendMail() auf
     * @param name
     * @param senderEmail
     * @param message
     * @throws MessagingException
     * @throws SQLException
     */
    @PostMapping("/feedbackMessage")
    @ResponseBody
    public String handleFeedback(
            @RequestParam("name") String name,
            @RequestParam("senderEmail") String senderEmail,
            @RequestParam("message") String message
    ) throws MessagingException, SQLException {

        SendFeedbackEmailService.sendEmail(senderEmail, name, message, myAccount, myPassword);

        return "Feedback erfolgreich empfangen!";
    }
}
