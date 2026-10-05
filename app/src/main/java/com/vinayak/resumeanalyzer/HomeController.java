package com.vinayak.resumeanalyzer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    private final ResumeAnalyzerService analyzerService;
    private final PdfService pdfService;
    private final OllamaService ollamaService;

    public HomeController(
            ResumeAnalyzerService analyzerService,
            PdfService pdfService,OllamaService ollamaService) {

        this.analyzerService = analyzerService;
        this.pdfService = pdfService;
        this.ollamaService = ollamaService;
    }

    @PostMapping("/analyze")
    public String analyze(
            @RequestParam("resumeFile") MultipartFile resumeFile,
            @RequestParam String jobDescription,
            Model model) throws IOException {

        String resumeText =
                pdfService.extractText(resumeFile);

        Map<String, Object> result =
                analyzerService.analyze(
                        resumeText,
                        jobDescription
                );
        String aiInsights = ollamaService.generateInsights(
                result.get("matchedSkills"),
                result.get("missingSkills"),
                result.get("matchPercentage")
        );

        model.addAttribute("aiInsights", aiInsights);

        model.addAttribute(
                "matchedSkills",
                result.get("matchedSkills")
        );

        model.addAttribute(
                "missingSkills",
                result.get("missingSkills")
        );

        model.addAttribute(
                "matchPercentage",
                result.get("matchPercentage")
        );

        return "result";
    }
}