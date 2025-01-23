package com.welfenhub.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EvaluationController {

    @PostMapping("/evaluation")
    public void getFeedback(
            @RequestParam("feedbackList") String feedbackValues,
            @RequestParam("text") String text) {

        System.out.println(feedbackValues + "\n" + text);

    }
}
