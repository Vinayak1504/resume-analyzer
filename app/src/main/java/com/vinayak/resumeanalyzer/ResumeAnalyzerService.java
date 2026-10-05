package com.vinayak.resumeanalyzer;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResumeAnalyzerService {

    private final SkillExtractionService skillExtractionService;

    public ResumeAnalyzerService(
            SkillExtractionService skillExtractionService) {

        this.skillExtractionService = skillExtractionService;
    }

    public Map<String, Object> analyze(
            String resume,
            String jobDescription) {

        Map<String, List<String>> extractedSkills =
                skillExtractionService.extractSkills(
                        resume,
                        jobDescription
                );

        List<String> resumeSkills =
                extractedSkills.getOrDefault(
                        "resumeSkills",
                        List.of()
                );

        List<String> requiredSkills =
                extractedSkills.getOrDefault(
                        "requiredSkills",
                        List.of()
                );

        Set<String> normalizedResumeSkills =
                normalizeSkills(resumeSkills);

        Set<String> normalizedRequiredSkills =
                normalizeSkills(requiredSkills);

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : normalizedRequiredSkills) {

            if (normalizedResumeSkills.contains(skill)) {

                matchedSkills.add(skill);

            } else {

                missingSkills.add(skill);
            }
        }

        double matchPercentage = 0;

        if (!normalizedRequiredSkills.isEmpty()) {

            matchPercentage =
                    ((double) matchedSkills.size()
                            / normalizedRequiredSkills.size())
                            * 100;
        }

        Map<String, Object> result = new HashMap<>();

        result.put("matchedSkills", matchedSkills);
        result.put("missingSkills", missingSkills);
        result.put(
                "matchPercentage",
                Math.round(matchPercentage)
        );

        return result;
    }

    private Set<String> normalizeSkills(
            List<String> skills) {

        Set<String> normalized = new LinkedHashSet<>();

        for (String skill : skills) {

            if (skill != null && !skill.isBlank()) {

                normalized.add(
                        skill.trim().toLowerCase()
                );
            }
        }

        return normalized;
    }
}