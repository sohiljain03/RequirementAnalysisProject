package com.example.demo.service;

import com.example.demo.dto.AnalysisResponse;
import com.example.demo.dto.UserStoriesResponse;
import com.example.demo.dto.LLDResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class RequirementsService {
    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    @Value("${openai.model}")
    private String openaiModel;

    private final RestTemplate restTemplate = new RestTemplate();

    // --- AI/NLP Integration with OpenAI ---
    private List<String> aiGenerateUserStories(String requirementsText) {
        String prompt = "Analyze the following requirement and generate 3 user stories with acceptance criteria in JSON array format.\nRequirement: " + requirementsText;
        String response = callOpenAI(prompt);
        // TODO: Parse response JSON to extract user stories (mock for now)
        return Arrays.asList(response.split("\\n"));
    }

    private String aiGenerateLLD(String requirementsText) {
        String prompt = "Generate a low-level design (LLD) for the following requirement.\nRequirement: " + requirementsText;
        String response = callOpenAI(prompt);
        return response;
    }

    private String callOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);
        Map<String, Object> body = new HashMap<>();
        body.put("model", openaiModel);
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        body.put("max_tokens", 512);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(openaiApiUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map choices = ((List<Map>) response.getBody().get("choices")).get(0);
                Map message = (Map) choices.get("message");
                return message.get("content").toString().trim();
            }
        } catch (Exception e) {
            return "[OpenAI API error: " + e.getMessage() + "]";
        }
        return "[No response from OpenAI]";
    }

    public AnalysisResponse analyze(String requirementsText) {
        return new AnalysisResponse(
            aiGenerateUserStories(requirementsText),
            aiGenerateLLD(requirementsText)
        );
    }

    public UserStoriesResponse generateUserStories(String requirementsText) {
        return new UserStoriesResponse(aiGenerateUserStories(requirementsText));
    }

    public LLDResponse generateLLD(String requirementsText) {
        return new LLDResponse(aiGenerateLLD(requirementsText));
    }

    public String getSampleRequirement() {
        return "As a user, I want to register an account so that I can access the system.";
    }
}
