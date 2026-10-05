# ResumeAI — AI-Powered Resume & Job Compatibility Analyzer

ResumeAI is a web-based application that analyzes a candidate's resume against a job description and provides an estimated skill match along with AI-generated insights.

The application uses **Java Spring Boot**, **Apache PDFBox**, and a **locally running Qwen3 0.6B model through Ollama**.

The system follows a hybrid approach: AI is used for understanding and extracting skills, while Java performs the actual deterministic skill comparison and match-score calculation.

---

## Features

- Upload a resume in PDF format
- Extract resume text using Apache PDFBox
- Enter a job description
- Extract resume skills and required job skills using Qwen3 0.6B
- Calculate skill-based match percentage using Java
- Identify matching skills
- Identify missing skills
- Generate AI-based resume improvement insights
- Run AI processing locally using Ollama
- No external paid AI API required
- Simple and responsive web interface
- Responsible-use disclaimer

---

## System Architecture

```text
                 ┌──────────────────────┐
                 │       User           │
                 │  Resume + Job JD     │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │   Thymeleaf UI       │
                 │    HTML / CSS        │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │    Spring Boot       │
                 │   HomeController     │
                 └──────────┬───────────┘
                            │
             ┌──────────────┼──────────────┐
             │              │              │
             ▼              ▼              ▼
       ┌───────────┐  ┌──────────────┐  ┌──────────────┐
       │ PdfService│  │ ResumeAnalyzer│  │OllamaService │
       │  PDFBox   │  │    Service    │  │              │
       └───────────┘  └───────┬──────┘  └──────┬───────┘
                              │                 │
                              │                 ▼
                              │        ┌─────────────────┐
                              │        │ Ollama + Qwen3  │
                              │        │     0.6B         │
                              │        └─────────────────┘
                              │
                              ▼
                    ┌─────────────────────┐
                    │  Deterministic      │
                    │  Skill Matching     │
                    │      in Java        │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Result Dashboard  │
                    │ Match + Skills + AI │
                    │      Insights       │
                    └─────────────────────┘
```

---

## How It Works

The application processes a resume through the following steps:

### 1. Resume Upload

The user uploads a PDF resume through the web interface.

### 2. PDF Text Extraction

Apache PDFBox extracts readable text from the uploaded PDF.

### 3. AI Skill Extraction

The extracted resume text and job description are sent to the locally running **Qwen3 0.6B** model through Ollama.

The model returns structured information containing:

```json
{
  "resumeSkills": [
    "Java",
    "Spring Boot",
    "SQL"
  ],
  "requiredSkills": [
    "Java",
    "Spring Boot",
    "Docker"
  ]
}
```

###  Skill Matching

The Java backend compares the extracted skill sets.

For example:

```text
Resume Skills:
Java
Spring Boot
SQL

Required Skills:
Java
Spring Boot
Docker
```

The system identifies:

```text
Matching Skills:
Java
Spring Boot

Missing Skills:
Docker
```

### 5. Match Score Calculation

The match percentage is calculated by Java using:

```text
Match Percentage =
(Number of Matching Required Skills /
 Number of Required Skills) × 100
```

For example:

```text
2 matching skills / 3 required skills × 100
