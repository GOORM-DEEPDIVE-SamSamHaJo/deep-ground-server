package com.samsamhajo.deepground.auth.controller;

import com.samsamhajo.deepground.auth.dto.*;
import com.samsamhajo.deepground.auth.service.AuthService;
import com.samsamhajo.deepground.auth.success.AuthSuccessCode;
import com.samsamhajo.deepground.global.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        Long memberId = authService.register(request);
        RegisterResponse response = new RegisterResponse(memberId, true);

        return ResponseEntity
                .status(AuthSuccessCode.REGISTER_SUCCESS.getStatus())
                .body(SuccessResponse.of(AuthSuccessCode.REGISTER_SUCCESS, response));
    }

    @GetMapping("/check-email")
    public ResponseEntity<SuccessResponse<Void>> checkEmailDuplicate(@RequestParam String email) {
        authService.checkEmailDuplicate(email);
        return ResponseEntity
                .status(AuthSuccessCode.EMAIL_AVAILABLE.getStatus())
                .body(SuccessResponse.of(AuthSuccessCode.EMAIL_AVAILABLE));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<SuccessResponse<Void>> checkNicknameDuplicate(@RequestParam String nickname) {
        authService.checkNicknameDuplicate(nickname);
        return ResponseEntity
                .status(AuthSuccessCode.NICKNAME_AVAILABLE.getStatus())
                .body(SuccessResponse.of(AuthSuccessCode.NICKNAME_AVAILABLE));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity
                .status(AuthSuccessCode.LOGIN_SUCCESS.getStatus())
                .body(SuccessResponse.of(AuthSuccessCode.LOGIN_SUCCESS, response));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<SuccessResponse<TokenRefreshResponse>> refreshAccessToken(
            @Valid @RequestBody TokenRefreshRequest request
    ) {
        TokenRefreshResponse response = authService.refreshAccessToken(request);
        return ResponseEntity
                .status(AuthSuccessCode.TOKEN_REFRESHED.getStatus())
                .body(SuccessResponse.of(AuthSuccessCode.TOKEN_REFRESHED, response));
    }
}
