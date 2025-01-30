package com.welfenhub.services;

import com.itextpdf.html2pdf.HtmlConverter;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConvertToPDFService {

    public static void convertToPdf(String htmlTemplate) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream("X:/git_repository/welfenhub_repo2/WA23/Project WelfenHub/pdfTest/test.pdf")) {
            HtmlConverter.convertToPdf(htmlTemplate, outputStream);
            System.out.println("PDF erfolgreich erstellt!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
