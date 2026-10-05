package com.vinayak.resumeanalyzer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class SkillExtractionService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public SkillExtractionService(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public Map<String, List<String>> extractSkills(
            String resume,
            String jobDescription) {

        String prompt = """
                Extract technical and professional skills from both texts.

                Resume:
                %s

                Job Description:
                %s

                Return ONLY valid JSON in exactly this format:

                {
                  "resumeSkills": ["skill1", "skill2"],
                  "requiredSkills": ["skill1", "skill2"]
                }

                Rules:
                - resumeSkills contains skills clearly present in the resume.
                - requiredSkills contains skills required or preferred by the job.
                - Do not add explanations.
                - Do not use markdown.
                - Do not invent skills.
                """.formatted(
                resume,
                jobDescription
        );

        Map<String, Object> request = Map.of(
                "model", "qwen3:0.6b",
                "prompt", prompt,
                "stream", false,
                "format", "json"
        );

        Map response = restClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null ||
                response.get("response") == null) {

            return Map.of(
                    "resumeSkills", List.of(),
                    "requiredSkills", List.of()
            );
        }

        try {

            String json =
                    response.get("response").toString();

            return objectMapper.readValue(
                    json,
                    new TypeReference<Map<String, List<String>>>() {}
            );

        } catch (Exception e) {

            System.out.println(
                    "Skill extraction failed: "
                            + e.getMessage()
            );

            return Map.of(
                    "resumeSkills", List.of(),
                    "requiredSkills", List.of()
            );
        }
    }
}