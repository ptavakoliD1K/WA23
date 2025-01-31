package com.welfenhub.services;

import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Component which converts html to pdf
 */


@Component
public class ConvertToPDFService {

    /**
     * converts html to pdf
     * @param htmlTemplate input which gets converted
     * @throws IOException
     */

    public static void convertToPdf(String htmlTemplate) throws IOException {
        // TODO Daten in Datenbank hochladen
        try (FileOutputStream outputStream = new FileOutputStream("X:/git_repository/welfenhub_repo2/WA23/Project WelfenHub/pdfTest/test.pdf")) {
            HtmlConverter.convertToPdf(htmlTemplate, outputStream);

            SendEvaluationByEmailService.sendEvaluationMail();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
