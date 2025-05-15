package com.welfenhub.services;

import com.itextpdf.html2pdf.HtmlConverter;
import com.welfenhub.repositories.EvaluationFilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Component which converts html to pdf
 */


@Component
public class ConvertToPDFService {

    @Autowired
    EvaluationFilesRepository evaluationFilesRepository;

    @Autowired
    SendEvaluationByEmailService sendEvaluationByEmailService;

    /**
     * converts html to pdf
     * @param htmlTemplate input which gets converted
     * @throws IOException
     */

    public void convertToPdf(String htmlTemplate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            HtmlConverter.convertToPdf(htmlTemplate, outputStream);

            LocalDateTime creationDate = LocalDateTime.now();

            saveOutputStreamToDatabase(outputStream, creationDate.toString());

            sendEvaluationByEmailService.sendEvaluationMail(creationDate.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveOutputStreamToDatabase(ByteArrayOutputStream byteArrayOutputStream, String date) {
        byte[] fileByteArray = byteArrayOutputStream.toByteArray();
        evaluationFilesRepository.saveFileToDatabase(fileByteArray, date);
    }


}
