package com.samsamhajo.deepground.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenRefreshResponse {

    @NotBlank
    private String accessToken;
    // 필요 시 refresh Token도 재발급
    @NotBlank
    private String refreshToken;

    public TokenRefreshResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
