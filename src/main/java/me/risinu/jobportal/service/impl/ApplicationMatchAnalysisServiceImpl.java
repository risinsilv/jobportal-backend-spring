package me.risinu.jobportal.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.risinu.jobportal.dto.openrouter.OpenRouterChatRequest;
import me.risinu.jobportal.dto.openrouter.OpenRouterChatResponse;
import me.risinu.jobportal.entity.ApplicationMatchDetails;
import me.risinu.jobportal.entity.Applications;
import me.risinu.jobportal.entity.JobPostings;
import me.risinu.jobportal.entity.JobSeekers;
import me.risinu.jobportal.repo.ApplicationMatchDetailsRepo;
import me.risinu.jobportal.repo.ApplicationsRepo;
import me.risinu.jobportal.service.ApplicationMatchAnalysisService;
import me.risinu.jobportal.service.OpenRouterService;
import me.risinu.jobportal.util.PdfExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ApplicationMatchAnalysisServiceImpl implements ApplicationMatchAnalysisService {

    private static final String SYSTEM_PROMPT = "You are an experienced technical recruiter and hiring analyst.\n\n" +
            "Your task is to evaluate how well a candidate matches a job description.\n" +
            "You must think like a recruiter:\n" +
            "- Focus on job-critical skills and experience\n" +
            "- Be objective and concise\n" +
            "- Do not hallucinate missing information\n" +
            "- Base decisions ONLY on the provided texts\n\n" +
            "You MUST return ONLY valid JSON.\n" +
            "Do not include explanations outside JSON.";

    private static final String USER_PROMPT_TEMPLATE = "Evaluate the candidate against the job description and return a structured hiring analysis.\n\n" +
            "### OUTPUT FORMAT (STRICT JSON)\n\n" +
            "{\n" +
            "  \"similarity_score\": 0.0,\n" +
            "  \"overall_match_level\": \"Weak | Moderate | Strong\",\n" +
            "  \"skills_analysis\": {\n" +
            "    \"matched_skills\": [],\n" +
            "    \"missing_skills\": [],\n" +
            "    \"optional_or_nice_to_have_missing\": []\n" +
            "  },\n" +
            "  \"experience_analysis\": {\n" +
            "    \"relevant_experience\": [],\n" +
            "    \"experience_gaps\": []\n" +
            "  },\n" +
            "  \"education_analysis\": {\n" +
            "    \"matched_education\": [],\n" +
            "    \"education_gaps\": []\n" +
            "  },\n" +
            "  \"strengths\": [],\n" +
            "  \"concerns\": [],\n" +
            "  \"overall_conclusion\": \"\",\n" +
            "  \"recruiter_recommendation\": \"\"\n" +
            "}\n\n" +
            "### SCORING GUIDELINES\n" +
            "- similarity_score must be between 0.0 and 1.0\n" +
            "- Skills match = 50%\n" +
            "- Experience match = 40%\n" +
            "- Education match = 10%\n\n" +
            "### OVERALL MATCH LEVEL\n" +
            "- Strong: score >= 0.75\n" +
            "- Moderate: score between 0.50 and 0.74\n" +
            "- Weak: score < 0.50\n\n" +
            "### IMPORTANT RULES\n" +
            "- Do NOT invent skills, roles, or experience\n" +
            "- If information is missing, list it as a gap\n" +
            "- Prioritize REQUIRED skills over optional ones\n" +
            "- Keep conclusions professional and recruiter-friendly\n" +
            "- overall_conclusion must be written in natural language (2–3 sentences)\n" +
            "- recruiter_recommendation must clearly say whether to proceed or not\n\n" +
            "---\n\n" +
            "### CANDIDATE CV\n" +
            "\"\"\"\n" +
            "{{CANDIDATE_CV_TEXT}}\n" +
            "\"\"\"\n\n" +
            "---\n\n" +
            "### JOB DESCRIPTION\n" +
            "\"\"\"\n" +
            "{{JOB_DESCRIPTION_TEXT}}\n" +
            "\"\"\"";

    private final ApplicationsRepo applicationsRepo;
    private final ApplicationMatchDetailsRepo matchDetailsRepo;
    private final OpenRouterService openRouterService;
    private final ObjectMapper objectMapper;

    @Value("${openrouter.model:google/gemma-3-27b-it:free}")
    private String openRouterModel;

    public ApplicationMatchAnalysisServiceImpl(
            ApplicationsRepo applicationsRepo,
            ApplicationMatchDetailsRepo matchDetailsRepo,
            OpenRouterService openRouterService,
            ObjectMapper objectMapper
    ) {
        this.applicationsRepo = applicationsRepo;
        this.matchDetailsRepo = matchDetailsRepo;
        this.openRouterService = openRouterService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Async("matchAnalysisExecutor")
    public void analyzeAndStoreForApplication(int applicationId) {
        Applications application = applicationsRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        JobPostings job = application.getJob();
        if (job == null) {
            throw new RuntimeException("Application has no job");
        }

        JobSeekers seeker = application.getJobSeeker();
        if (seeker == null) {
            throw new RuntimeException("Application has no job seeker");
        }

        String jobText = buildJobDescriptionText(job);
        String cvText = extractCvText(seeker);

        String userPrompt = USER_PROMPT_TEMPLATE
                .replace("{{CANDIDATE_CV_TEXT}}", safePromptBlock(cvText))
                .replace("{{JOB_DESCRIPTION_TEXT}}", safePromptBlock(jobText));

        OpenRouterChatRequest req = new OpenRouterChatRequest(
                openRouterModel,
                List.of(
                        new OpenRouterChatRequest.Message(
                                "system",
                                List.of(new OpenRouterChatRequest.ContentPart("text", SYSTEM_PROMPT, null))
                        ),
                        new OpenRouterChatRequest.Message(
                                "user",
                                List.of(new OpenRouterChatRequest.ContentPart("text", userPrompt, null))
                        )
                )
        );

        OpenRouterChatResponse res = openRouterService.chatCompletions(req);
        String content = (res != null && res.getChoices() != null && !res.getChoices().isEmpty() && res.getChoices().get(0).getMessage() != null)
                ? res.getChoices().get(0).getMessage().getContent()
                : null;

        // Log the raw response for debugging
        System.out.println("Raw response from OpenRouter: " + content);

        if (content == null || content.isBlank()) {
            throw new RuntimeException("No response or empty response from OpenRouter");
        }

        // Some models/providers may wrap JSON with extra text. Extract the JSON object only.
        String jsonContent = extractJsonObject(content);
        System.out.println("Sanitized JSON content: " + jsonContent);

        // Validate JSON (strict requirement) and extract key fields for columns.
        JsonNode root;
        try {
            root = objectMapper.readTree(jsonContent);
        } catch (Exception ex) {
            System.err.println("Invalid JSON response (sanitized): " + jsonContent);
            throw new RuntimeException("OpenRouter returned non-JSON or invalid JSON", ex);
        }

        Double similarityScore = root.has("similarity_score") && root.get("similarity_score").isNumber()
                ? root.get("similarity_score").asDouble()
                : null;

        String overallMatchLevel = root.has("overall_match_level") ? root.get("overall_match_level").asText(null) : null;

        ApplicationMatchDetails details = matchDetailsRepo.findByApplication_ApplicationId(applicationId)
                .orElseGet(ApplicationMatchDetails::new);

        details.setApplication(application);
        details.setSimilarityScore(similarityScore);
        details.setOverallMatchLevel(overallMatchLevel);
        details.setAnalysisJson(jsonContent);

        matchDetailsRepo.save(details);

        // Keep the bidirectional reference in sync (optional)
        application.setMatchDetails(details);
        applicationsRepo.save(application);
    }

    private String buildJobDescriptionText(JobPostings job) {
        StringBuilder sb = new StringBuilder();
        if (job.getTitle() != null) sb.append("Title: ").append(job.getTitle()).append("\n\n");
        if (job.getLocation() != null) sb.append("Location: ").append(job.getLocation()).append("\n\n");
        if (job.getDescription() != null) sb.append("Description: ").append(job.getDescription()).append("\n\n");
        if (job.getResponsibilities() != null) sb.append("Responsibilities: ").append(job.getResponsibilities()).append("\n\n");
        if (job.getRequirements() != null) sb.append("Requirements: ").append(job.getRequirements()).append("\n\n");
        if (job.getNiceToHave() != null) sb.append("Nice to have: ").append(job.getNiceToHave()).append("\n\n");
        if (job.getOther() != null) sb.append("Other: ").append(job.getOther()).append("\n\n");
        return sb.toString().trim();
    }

    private String extractCvText(JobSeekers seeker) {
        // Prefer resumeUrl if present, otherwise fallback to your existing convention
        String path =  "userResume/cv_" + seeker.getId() + ".pdf";

        File f = new File(path);
        // Log the absolute path for debugging
        System.out.println("Looking for CV at path: " + f.getAbsolutePath());

        if (!f.exists() || !f.isFile()) {
            throw new RuntimeException("CV file not found for job seeker: " + seeker.getId() + ". Path: " + f.getAbsolutePath());
        }

        try {
            String text = PdfExtractor.extractTextFromPdf(f.getPath());
            if (text == null || text.isBlank()) {
                throw new RuntimeException("CV PDF is empty or unreadable. Path: " + f.getAbsolutePath());
            }
            return text;
        } catch (Exception e) {
            throw new RuntimeException("Error reading CV file for job seeker: " + seeker.getId() + ". Path: " + f.getAbsolutePath(), e);
        }
    }

    private String safePromptBlock(String s) {
        if (s == null) return "";
        // Avoid accidentally closing the triple quotes used in the template.
        return s.replace("\"\"\"", "\"\"");
    }

    /**
     * Extracts the first JSON object from a possibly wrapped LLM response.
     * Removes anything before the first '{' and after the last '}' (inclusive).
     */
    private String extractJsonObject(String raw) {
        if (raw == null) return null;
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start < 0 || end < 0 || end <= start) {
            // Nothing usable; return original so the caller's JSON parse throws with logged content.
            return raw;
        }
        return raw.substring(start, end + 1).trim();
    }
}
