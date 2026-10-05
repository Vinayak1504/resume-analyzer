package com.vinayak.resumeanalyzer;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OllamaService {

    private final RestClient restClient;

    public OllamaService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public String generateInsights(
            Object matchedSkills,
            Object missingSkills,
            Object matchPercentage) {

        String prompt = """
            You are a concise resume analysis assistant.

            Analyze these already-calculated results.

            Match Score:
            %s

            Matching Skills:
            %s

            Missing Skills:
            %s

            Provide exactly three short sections:

            Strengths:
            - Mention the strongest matching skills.

            Areas to Improve:
            - Mention the most important missing skills.

            Resume Suggestions:
            - Give 2 practical suggestions.

            Keep the response under 150 words.
            Do not make hiring decisions.
            Do not claim the candidate will get the job.
            """.formatted(
                matchPercentage,
                matchedSkills,
                missingSkills
        );

        Map<String, Object> request = Map.of(
                "model", "qwen3:0.6b",
                "prompt", prompt,
                "stream", false
        );

        Map response = restClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null ||
                response.get("response") == null) {

            return "Unable to generate AI insights.";
        }

        return response.get("response").toString();
    }
}