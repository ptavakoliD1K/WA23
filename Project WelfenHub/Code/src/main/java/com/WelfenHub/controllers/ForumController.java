package com.WelfenHub.controllers;

import com.WelfenHub.models.Post;
import com.WelfenHub.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


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

    // Seite mit Semesterübersicht für ein Fach
    @GetMapping("/forum/{subject}")
    public String getSemesterOverview(@PathVariable String subject, Model model) {
        model.addAttribute("subject", subject);

        // Beispiel für das Abrufen des zuletzt kommentierten Posts
        Post latestPost = postService.getLatestPostForSubject(subject);
        if (latestPost != null) {
            model.addAttribute("latestPost", latestPost);
        } else {
            model.addAttribute("latestPost", new Post()); // Setze ein leeres Post-Objekt als Fallback
        }

        return "semester-overview"; // Die Seite mit der Semesterübersicht
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
        return "subject"; // Die Seite mit allen Posts zu einem bestimmten Kurs
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
