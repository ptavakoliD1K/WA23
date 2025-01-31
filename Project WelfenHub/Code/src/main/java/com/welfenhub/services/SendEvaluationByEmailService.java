package com.welfenhub.services;

import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
public class SendEvaluationByEmailService {

    public static void sendEvaluationMail() throws FileNotFoundException {
        try {
            InputStream inputStream = new FileInputStream("X:/git_repository/welfenhub_repo2/WA23/Project WelfenHub/pdfTest/test.pdf");


            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
