package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasModule {
    private Long id;
    private String name;

    @JsonProperty("items") // This is mandatory for nested Canvas API lists
    private List<CanvasModuleItem> items;
}