package com.example.homeworkstrateguistsbgradle;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class HomeworkController {

    private final CanvasService canvasService;
    private final GoogleCalendarService calendarService;

    public HomeworkController(CanvasService canvasService, GoogleCalendarService calendarService) {
        this.canvasService = canvasService;
        this.calendarService = calendarService;
    }

    @GetMapping("/status")
    public String status() {
        return "Backend Connected!";
    }
}