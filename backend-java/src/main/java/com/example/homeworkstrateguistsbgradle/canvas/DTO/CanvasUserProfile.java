package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasUserProfile {
    private Integer id;
    private String name;

    @JsonProperty("primary_email")
    private String email;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private String bio;
}