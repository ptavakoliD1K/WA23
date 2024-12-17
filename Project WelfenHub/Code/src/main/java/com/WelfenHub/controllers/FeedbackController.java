package com.WelfenHub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class FeedbackController {
    // Zeigt die Feedback-Seite an
    @GetMapping("/feedback")
    public String showFeedbackForm() {
        return "feedback"; // Zeigt feedback.html an
    }

    // Verarbeitet das abgeschickte Feedback
    @PostMapping("/feedback")
    public String handleFeedback(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("message") String message,
            Model model) {

        // Optional: Feedback-Daten speichern oder per E-Mail senden
        System.out.println("Feedback erhalten:");
        System.out.println("Name: " + name);
        System.out.println("E-Mail: " + email);
        System.out.println("Nachricht: " + message);

        // Bestätigung an die View senden
        model.addAttribute("successMessage", "Vielen Dank für dein Feedback, " + name + "!");

        return "feedback"; // Zurück zur Feedback-Seite
    }
}
