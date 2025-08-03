package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.RequirementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/requirements")
@CrossOrigin(origins = "http://localhost:4200") // Enable CORS for Angular dev
public class RequirementsController {

    @Autowired
    private RequirementsService requirementsService;

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyzeRequirements(@RequestBody RequirementRequest request) {
        AnalysisResponse result = requirementsService.analyze(request.getRequirementsText());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/sample")
    public ResponseEntity<String> getSampleRequirement() {
        return ResponseEntity.ok(requirementsService.getSampleRequirement());
    }

    @PostMapping("/user-stories")
    public ResponseEntity<UserStoriesResponse> generateUserStories(@RequestBody RequirementRequest request) {
        return ResponseEntity.ok(requirementsService.generateUserStories(request.getRequirementsText()));
    }

    @PostMapping("/lld")
    public ResponseEntity<LLDResponse> generateLLD(@RequestBody RequirementRequest request) {
        return ResponseEntity.ok(requirementsService.generateLLD(request.getRequirementsText()));
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}
