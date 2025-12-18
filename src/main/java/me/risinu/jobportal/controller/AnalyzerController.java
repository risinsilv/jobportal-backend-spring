package me.risinu.jobportal.controller;

import me.risinu.jobportal.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analyze")
public class AnalyzerController {

    @Autowired
    private OpenAIService openAIService;

    @GetMapping("/{userId}")
    public String analyzeCv(@PathVariable String userId) {
        return openAIService.analyzePdf(userId);
    }
}

