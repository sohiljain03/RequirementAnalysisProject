package com.example.demo.service;

import com.example.demo.dto.AnalysisResponse;
import com.example.demo.dto.UserStoriesResponse;
import com.example.demo.dto.LLDResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
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
        String prompt = "Analyze the following requirement and generate all possible  user stories with acceptance criteria in JSON array format.\nRequirement: " + requirementsText;
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
        // Mocked data for testing API without OpenAI
        List<String> userStories = Arrays.asList(
            "As a user, I want to register an account so that I can access the system.",
            "As an admin, I want to approve new user registrations so that only valid users can access the system.",
            "As a user, I want to receive a confirmation email after registration so that I know my account is active."
        );
        String lld = "Class: UserRegistrationController\nClass: UserService\nClass: UserRepository\nFlow: User submits registration form -> Controller -> Service -> Repository -> EmailService";
        return new AnalysisResponse(userStories, lld);
        // Uncomment below to use OpenAI integration
        // return new AnalysisResponse(
        //     aiGenerateUserStories(requirementsText),
        //     aiGenerateLLD(requirementsText)
        // );
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

    public byte[] generateLLDWordDoc(String requirementsText) {
        // 1. Get LLD and PlantUML from OpenAI
        String prompt = "Generate a detailed low-level design (LLD) for the following requirement, including a PlantUML class diagram. Respond in JSON with 'lld' and 'plantuml' fields.\nRequirement: " + requirementsText;
        String response = callOpenAI(prompt);
        String lldText = response;
        String plantUmlCode = null;
        // Simple extraction (replace with JSON parsing if needed)
        if (response.contains("plantuml")) {
            int idx = response.indexOf("@startuml");
            int end = response.indexOf("@enduml") + 7;
            if (idx >= 0 && end > idx) {
                plantUmlCode = response.substring(idx, end);
            }
        }
        // 2. Render PlantUML to image
        byte[] diagramImg = null;
        if (plantUmlCode != null) {
            try {
                String encoded = java.util.Base64.getUrlEncoder().encodeToString(plantUmlCode.getBytes());
                String url = "https://www.plantuml.com/plantuml/png/~h" + encoded;
                diagramImg = IOUtils.toByteArray(new URL(url));
            } catch (Exception e) {
                diagramImg = null;
            }
        }
        // 3. Create Word doc
        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            run.setText("Low Level Design (LLD):");
            run.addBreak();
            run.setText(lldText);
            run.addBreak();
            if (diagramImg != null) {
                run.addBreak();
                run.setText("Class Diagram:");
                run.addBreak();
                run.addPicture(new ByteArrayInputStream(diagramImg), XWPFDocument.PICTURE_TYPE_PNG, "diagram.png", 400 * 9525, 300 * 9525);
            }
            doc.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
