package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasAssignment {
    private Long id;
    private String name;

    @JsonProperty("due_at")
    private OffsetDateTime dueAt;

    @JsonProperty("course_id")
    private Long courseId;

    private String description;

    /**
     * Extracts only the text from the HTML description.
     * Useful for mobile previews or text-only displays.
     */
    @JsonProperty("plain_text_description")
    public String getPlainTextDescription() {
        if (this.description == null || this.description.isEmpty()) {
            return "";
        }
        return Jsoup.parse(this.description).text();
    }

    /**
     * Differentiates and returns only links that point to Canvas internal files.
     */
    @JsonProperty("canvas_file_links")
    public List<String> getCanvasFileLinks() {
        if (this.description == null || this.description.isEmpty()) {
            return List.of();
        }
        Document doc = Jsoup.parse(this.description);
        // We filter for the specific class Canvas uses for file attachments
        return doc.select("a.instructure_file_link")
                .stream()
                .map(element -> element.attr("href"))
                .collect(Collectors.toList());
    }

    /**
     * Differentiates and returns links to external websites (YouTube, Tutorialspoint, etc.)
     */
    @JsonProperty("external_links")
    public List<String> getExternalLinks() {
        if (this.description == null || this.description.isEmpty()) {
            return List.of();
        }
        Document doc = Jsoup.parse(this.description);
        Elements allLinks = doc.select("a");

        return allLinks.stream()
                // If it DOESN'T have the Canvas file class, it's external
                .filter(element -> !element.hasClass("instructure_file_link"))
                .map(element -> element.attr("href"))
                .collect(Collectors.toList());
    }
}
