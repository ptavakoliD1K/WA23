package com.welfenhub.controllers;

import com.welfenhub.models.Post;
import com.welfenhub.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.List;
import java.util.TreeMap;

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
                                    @RequestParam(value = "sort", defaultValue = "created") String sort,
                                    Model model) {

        List<Post> posts;
        if ("likes".equals(sort)) {
            posts = postService.getPostsByCourseSortedByLikes(course);
        } else {
            posts = postService.getPostsByCourseSortedByDate(course);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("course", course);
        model.addAttribute("semester", semester);
        model.addAttribute("subject", subject);
        model.addAttribute("sort", sort); // für das Dropdown ausgewählt
        model.addAttribute("coursesBySemester", getCoursesForSubject(subject));

        return "subject";
    }




    private Map<Integer, List<String>> getCoursesForSubject(String subject) {
        switch (subject.toLowerCase()) {
            case "bwl":
                return new TreeMap<>(Map.of(
                        1, List.of("BWL I", "BWL II", "Marketing", "Finanzmanagement"),
                        2, List.of("Investition", "Rechnungswesen", "Controlling", "Organisationsentwicklung")
                ));
            case "wirtschaftsinformatik":
                return new TreeMap<>(Map.of(
                        1, List.of("Grundlagen der Computertechnik", "Engineering Management", "Grundlagen Mathematik", "Technische Kommunikation", "Grundlagen sicherer Programmierung", "Einstieg in die Projektarbeit", "BWL I"),
                        2, List.of("Programmstrukturen", "Spezielle Mathematik", "Technische Kommunikation", "Grundlagen sicherer Programmierung", "Secruity Engineering", "Einstieg in die Projektarbeit"),
                        3, List.of("Anwendungsrealisierung", "Datenbank Programmierung", "Secruity Engineering", "Projektumsetzung", "BWL II"),
                        4, List.of("Alternative Daten- und Programmieransätze", "Sicherheit verteilter Systeme", "Secruity Management", "Projektumsetzung", "BWL II", "Development Engineering"),
                        5, List.of("IT-gestütztes Management", "Verknüpfung der Wirtschaftsinformatik", "Geschäftliche Kommunikation", "Developement Practice", "Vertiefungen"),
                        6, List.of("ITSM", "Geschäftliche Kommunikation", "Development Test", "Bachelorarbeit", "Vertiefungen")
                        ));
            case "sonstiges":
                return new TreeMap<>(Map.of(
                        1, List.of("Kreatives Schreiben", "Philosophie", "Psychologie", "Design Thinking"),
                        2, List.of("Fotografie", "Musiktheorie", "Moderne Kunst", "Soziologie")
                ));
            default:
                return new TreeMap<>(Map.of()); // Leere Map für unbekannte Fächer
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

        // ✅ Sidebar retten
        Map<Integer, List<String>> coursesBySemester = getCoursesForSubject(subject);
        model.addAttribute("sidepanelCourses", coursesBySemester.get(semester));
        model.addAttribute("coursesBySemester", coursesBySemester);

        return "subject";
    }


    @GetMapping("/forum/{subject}/all")
    public String viewAllPostsBySubjectDescending(@PathVariable("subject") String subject, Model model) {
        // Posts holen, absteigend nach createdAt sortiert
        List<Post> sortedPosts = postService.getPostsBySubjectDescending(subject);
        model.addAttribute("posts", sortedPosts);
        // Für den Seitentitel oder Anzeige:
        model.addAttribute("currentSubject", subject);
        // Zeige ein eigenes Template "subject-posts.html"
        return "subject-posts";
    }

}
