package com.welfenhub.controllers;

import com.welfenhub.services.EvaluationService;
import com.welfenhub.services.SendEvaluationByEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class EvaluationController {

    @Autowired
    EvaluationService evaluationService;

    @PostMapping("/evaluation")
    public void getFeedback(
            @RequestParam("feedbackList") String feedbackValues,
            @RequestParam("text") String text) throws IOException {

        evaluationService.generateHtml(feedbackValues, text);

    }
}
