package com.welfenhub.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Service, which edits html to match the lecturer rating
 */
@Service
public class EvaluationService {

    @Autowired
    private ConvertToPDFService convertToPDFService;

    /**
     * generates html with values of the lecturer rating and calls convertToPdf
     */
    public void generateHtml(String valueJson, String text) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> mapValues = objectMapper.readValue(valueJson, Map.class);

        // ✅ Lade Template über Classpath
        String htmlTemplate;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("templates/fragments/dozentenEvaluationWADokument.html")) {
            if (is == null) {
                throw new IOException("Template not found in resources: templates/fragments/dozentenEvaluationWADokument.html");
            }
            htmlTemplate = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        htmlTemplate = htmlTemplate.replace("{{lehrveranstaltung}}", mapValues.get("Lehrveranstaltung"));
        htmlTemplate = htmlTemplate.replace("{{dozent}}", mapValues.get("Dozent"));
        htmlTemplate = htmlTemplate.replace("{{semester}}", mapValues.get("Semester"));
        htmlTemplate = htmlTemplate.replace("{{jahrgang}}", mapValues.get("Jahrgang"));
        htmlTemplate = htmlTemplate.replace("{{fachrichtung}}", mapValues.get("Fachrichtung"));
        htmlTemplate = htmlTemplate.replace("{{text}}", text);

        final String CHECKED_TRUE = "checked=\"true\"";

        for (int i = 1; i <= 5; i++) {
            htmlTemplate = htmlTemplate.replace("{{checked1" + i + "}}", i == parse(mapValues, "gesamteindruckLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked2" + i + "}}", i == parse(mapValues, "vergleichLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked3" + i + "}}", i == parse(mapValues, "klimaLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked4" + i + "}}", i == parse(mapValues, "strukturierungLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked5" + i + "}}", i == parse(mapValues, "detailLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked6" + i + "}}", i == parse(mapValues, "detailInfoLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked7" + i + "}}", i == parse(mapValues, "umfangLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked8" + i + "}}", i == parse(mapValues, "sachLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked9" + i + "}}", i == parse(mapValues, "praxisbezugLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked10" + i + "}}", i == parse(mapValues, "interesseWecken") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked11" + i + "}}", i == parse(mapValues, "presentationLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked12" + i + "}}", i == parse(mapValues, "explanationLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked13" + i + "}}", i == parse(mapValues, "questionLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked14" + i + "}}", i == parse(mapValues, "aktiveBeteiligungLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked15" + i + "}}", i == parse(mapValues, "kompetentLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked16" + i + "}}", i == parse(mapValues, "themaLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked17" + i + "}}", i == parse(mapValues, "beteiligungLabel") ? CHECKED_TRUE : "");
            htmlTemplate = htmlTemplate.replace("{{checked18" + i + "}}", i == parse(mapValues, "lernerfolgLabel") ? CHECKED_TRUE : "");
        }

        convertToPDFService.convertToPdf(htmlTemplate);
    }

    private int parse(Map<String, String> map, String key) {
        return Integer.parseInt(map.getOrDefault(key, "0"));
    }
}
