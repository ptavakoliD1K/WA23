package com.welfenhub.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Service, which edits to html to match the lecturer rating
 */

@Service
public class EvaluationService {

    /**
     * private constructor for clean code
     */

    EvaluationService() {
    }

     @Autowired
     ConvertToPDFService convertToPDFService;

    /**
     * generates html which right values of the lecturer rating and start convertToPdf
     * @param valueJson
     * @param text
     * @throws IOException
     */

    public void generateHtml(String valueJson, String text) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> mapValues = objectMapper.readValue(valueJson, Map.class);

        String htmlTemplate = new String(Files.readAllBytes(Path.of("/templates/fragments/dozentenEvaluationWADokument.html")));

        String valueLehrveranstaltung = mapValues.get("Lehrveranstaltung");
        String valueDozent = mapValues.get("Dozent");
        String valueSemester = mapValues.get("Semester");
        String valueJahrgang = mapValues.get("Jahrgang");
        String valueFachrichtung = mapValues.get("Fachrichtung");
        int valueGesamtEindruck = Integer.parseInt(mapValues.get("gesamteindruckLabel"));
        int valueVergleich = Integer.parseInt(mapValues.get("vergleichLabel"));
        int valueKlima = Integer.parseInt(mapValues.get("klimaLabel"));
        int valueStrukturierung = Integer.parseInt(mapValues.get("strukturierungLabel"));
        int valueDetail = Integer.parseInt(mapValues.get("detailLabel"));
        int valueDetailInformation = Integer.parseInt(mapValues.get("detailInfoLabel"));
        int valueUmfang = Integer.parseInt(mapValues.get("umfangLabel"));
        int valueSach = Integer.parseInt(mapValues.get("sachLabel"));
        int valuePraxis = Integer.parseInt(mapValues.get("praxisbezugLabel"));
        int valueInteresseWecken = Integer.parseInt(mapValues.get("interesseWecken"));
        int valuePresentation = Integer.parseInt(mapValues.get("presentationLabel"));
        int valueExplanation = Integer.parseInt(mapValues.get("explanationLabel"));
        int valueQuestion = Integer.parseInt(mapValues.get("questionLabel"));
        int valueActiveBeteiligung = Integer.parseInt(mapValues.get("aktiveBeteiligungLabel"));
        int valueKompetent = Integer.parseInt(mapValues.get("kompetentLabel"));
        int valueThema = Integer.parseInt(mapValues.get("themaLabel"));
        int valueBeteiligung = Integer.parseInt(mapValues.get("beteiligungLabel"));
        int valueLernerfolg = Integer.parseInt(mapValues.get("lernerfolgLabel"));

        htmlTemplate = htmlTemplate.replace("{{lehrveranstaltung}}", valueLehrveranstaltung);
        htmlTemplate = htmlTemplate.replace("{{dozent}}", valueDozent);
        htmlTemplate = htmlTemplate.replace("{{semester}}", valueSemester);
        htmlTemplate = htmlTemplate.replace("{{jahrgang}}", valueJahrgang);
        htmlTemplate = htmlTemplate.replace("{{fachrichtung}}", valueFachrichtung);
        htmlTemplate = htmlTemplate.replace("{{text}}", text);

        final String CHECKED_TRUE = "checked=\"true\"";

        for (int i = 1; i <= 5; i++) {
            String checked1 = (i == valueGesamtEindruck) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked1" + i + "}}", checked1);

            String checked2 = (i == valueVergleich) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked2" + i + "}}", checked2);

            String checked3 = (i == valueKlima) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked3" + i + "}}", checked3);

            String checked4 = (i == valueStrukturierung) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked4" + i + "}}", checked4);

            String checked5 = (i == valueDetail) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked5" + i + "}}", checked5);

            String checked6 = (i == valueDetailInformation) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked6" + i + "}}", checked6);

            String checked7 = (i == valueUmfang) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked7" + i + "}}", checked7);

            String checked8 = (i == valueSach) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked8" + i + "}}", checked8);

            String checked9 = (i == valuePraxis) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked9" + i + "}}", checked9);

            String checked10 = (i == valueInteresseWecken) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked10" + i + "}}", checked10);

            String checked11 = (i == valuePresentation) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked11" + i + "}}", checked11);

            String checked12 = (i == valueExplanation) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked12" + i + "}}", checked12);

            String checked13 = (i == valueQuestion) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked13" + i + "}}", checked13);

            String checked14 = (i == valueActiveBeteiligung) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked14" + i + "}}", checked14);

            String checked15 = (i == valueKompetent) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked15" + i + "}}", checked15);

            String checked16 = (i == valueThema) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked16" + i + "}}", checked16);

            String checked17 = (i == valueBeteiligung) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked17" + i + "}}", checked17);

            String checked18 = (i == valueLernerfolg) ? CHECKED_TRUE : "";
            htmlTemplate = htmlTemplate.replace("{{checked18" + i + "}}", checked18);

        }

        convertToPDFService.convertToPdf(htmlTemplate);

    }
}
