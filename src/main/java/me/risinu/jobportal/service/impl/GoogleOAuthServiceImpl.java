package me.risinu.jobportal.service.impl;

import me.risinu.jobportal.dto.GoogleUserInfoDto;
import me.risinu.jobportal.service.GoogleOAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GoogleOAuthServiceImpl implements GoogleOAuthService {

    private final RestClient restClient;

    @Value("${google.client-id}")
    private String clientId;

    public GoogleOAuthServiceImpl(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public GoogleUserInfoDto validateIdTokenAndGetUser(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new RuntimeException("Missing Google idToken");
        }

        GoogleUserInfoDto userInfo = restClient.get()
                .uri("https://oauth2.googleapis.com/tokeninfo?id_token={idToken}", idToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(GoogleUserInfoDto.class);

        if (userInfo == null || userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
            throw new RuntimeException("Google token validation failed (missing email)");
        }

        // Ensure token was issued for this app.
        if (userInfo.getAud() == null || !userInfo.getAud().equals(clientId)) {
            throw new RuntimeException("Google token audience mismatch");
        }

        return userInfo;
    }
}
