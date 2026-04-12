package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasCourse {
    private Long id;
    private String name;

    @JsonProperty("course_code")
    private String courseCode;

    @JsonProperty("enrollment_term_id")
    private Long termId;

    // Add this field to handle the banner image
    @JsonProperty("image_download_url")
    private String imageDownloadUrl;
}



