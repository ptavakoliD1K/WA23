package com.welfenhub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";  // This refers to src/main/resources/templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // This refers to src/main/resources/templates/login.html
    }

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin";
    }

    @GetMapping("/fileshare")
    public String fileshare() {
        return "fileshare";
    }

    @GetMapping("/feedback")
    public String feedback() {
        return "feedback";
    }

    @GetMapping("/dozenten_bewertung")
    public String dozentenBewertung() {
        return "dozentenFeedback";
    }

    @GetMapping("/ereignisse")
    public String ereignisse() {
        return "ereignisse";
    }

    @GetMapping("/impressum")
    public String impressum() {
        return "impressum";
    }

    @GetMapping("/jobs")
    public String jobs() {
        return "jobPage";
    }
}
