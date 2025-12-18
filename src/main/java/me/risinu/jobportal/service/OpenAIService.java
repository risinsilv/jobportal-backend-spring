package me.risinu.jobportal.service;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import me.risinu.jobportal.util.PdfExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class OpenAIService {

    @Value("${openai.api.key:}")
    private String configuredApiKey; // can be blank

    private OpenAiService openAiService; // lazily initialized

    private synchronized void ensureClient() {
        if (openAiService != null) return;
        // Use ONLY the Spring property; no environment fallback per requirement
        String key = configuredApiKey;
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("OpenAI API key missing (set 'openai.api.key' in application.properties or externalized config).");
        }
        openAiService = new OpenAiService(key, Duration.ofSeconds(60));
    }

    public String analyzePdf(String userId) {
        try {
            // 1. Locate CV
            String filePath = "userResume/cv_" + userId + ".pdf";

            // 2. Extract text
            String text = PdfExtractor.extractTextFromPdf(filePath);
            if (text == null || text.isBlank()) {
                return "PDF is empty or unreadable";
            }

            // 3. Init client lazily
            ensureClient();

            // 4. Send to model (use a supported model name)
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-5")
                    .messages(List.of(
                            new ChatMessage("system", "You are a resume parsing analyzer. Give a parse score (0-100) and a short explanation."),
                            new ChatMessage("user", "Analyze this resume and return a parse score:\n\n" + text)
                    ))
                    .build();

            List<ChatCompletionChoice> choices = openAiService.createChatCompletion(request).getChoices();
            if (choices == null || choices.isEmpty()) {
                return "No response from OpenAI";
            }
            return choices.get(0).getMessage().getContent();

        } catch (IllegalStateException ise) {
            return "Configuration error: " + ise.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error analyzing PDF: " + e.getMessage();
        }
    }
}
