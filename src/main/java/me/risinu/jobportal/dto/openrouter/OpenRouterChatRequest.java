package me.risinu.jobportal.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenRouterChatRequest {

    private String model;

    private List<Message> messages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private List<ContentPart> content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentPart {
        private String type; // "text" | "image_url"

        // for type=text
        private String text;

        // for type=image_url
        @JsonProperty("image_url")
        private ImageUrl imageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageUrl {
        private String url;
    }
}

