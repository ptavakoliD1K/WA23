package com.WelfenHub.controllers;

import com.WelfenHub.models.Post;
import com.WelfenHub.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.List;


import java.util.List;

@Controller
public class ForumController {

    @Autowired
    private PostService postService;

    // Seite zur Auswahl von BWL, Wirtschaftsinformatik und Sonstiges
    @GetMapping("/forum")
    public String getSubjectOverview() {
        return "start"; // Die Startseite mit Fachauswahl
    }

    @GetMapping("/forum/{subject}")
    public String getSemesterOverview(@PathVariable String subject, Model model) {
        model.addAttribute("subject", subject);
        model.addAttribute("coursesBySemester", getCoursesForSubject(subject));
        return "semester-overview";
    }

    // Zeige alle Posts für ein bestimmtes Fach und Semester
    @GetMapping("/forum/{subject}/{semester}/course/{course}")
    public String getPostsForCourse(@PathVariable String subject,
                                    @PathVariable int semester,
                                    @PathVariable String course,
                                    Model model) {
        // Lade die Posts zu dem Kurs und Semester
        List<Post> posts = postService.getPostsByCourse(course);
        model.addAttribute("posts", posts);
        model.addAttribute("course", course);
        model.addAttribute("semester", semester);
        model.addAttribute("subject", subject);

        // Lade die Fächer für das aktuelle Semester und alle Semester
        Map<Integer, List<String>> coursesBySemester = getCoursesForSubject(subject);
        model.addAttribute("sidepanelCourses", coursesBySemester.get(semester)); // Kurse des aktuellen Semesters
        model.addAttribute("coursesBySemester", coursesBySemester); // Alle Semester

        return "subject"; // Die Seite mit allen Posts zu einem bestimmten Kurs
    }


    private Map<Integer, List<String>> getCoursesForSubject(String subject) {
        switch (subject.toLowerCase()) {
            case "bwl":
                return Map.of(
                        1, List.of("BWL I", "BWL II", "Marketing", "Finanzmanagement"),
                        2, List.of("Investition", "Rechnungswesen", "Controlling", "Organisationsentwicklung")
                );
            case "wirtschaftsinformatik":
                return Map.of(
                        1, List.of("Programmieren I", "Datenbanken", "IT-Management", "Webentwicklung"),
                        2, List.of("Programmieren II", "KI-Grundlagen", "Cloud Computing", "Cybersecurity")
                );
            case "sonstiges":
                return Map.of(
                        1, List.of("Kreatives Schreiben", "Philosophie", "Psychologie", "Design Thinking"),
                        2, List.of("Fotografie", "Musiktheorie", "Moderne Kunst", "Soziologie")
                );
            default:
                return Map.of(); // Leere Map für unbekannte Fächer
        }
    }

    @GetMapping("/forum/{subject}/{semester}/course/{course}/search")
    public String searchInCourse(@PathVariable String subject,
                                 @PathVariable int semester,
                                 @PathVariable String course,
                                 @RequestParam("query") String query,
                                 Model model) {
        List<Post> posts = postService.searchPostsByTitle(query);
        model.addAttribute("posts", posts);
        model.addAttribute("course", course);
        model.addAttribute("semester", semester);
        model.addAttribute("subject", subject);

        // Return the correct template, for example, "subject" if it displays the posts
        return "subject";
    }

}
