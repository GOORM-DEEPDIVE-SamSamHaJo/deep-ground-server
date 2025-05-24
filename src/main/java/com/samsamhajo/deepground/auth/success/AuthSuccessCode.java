package com.samsamhajo.deepground.auth.success;

import com.samsamhajo.deepground.global.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthSuccessCode implements SuccessCode {

    // 회원가입 성공
    REGISTER_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),

    // 이메일 중복 검사 성공
    EMAIL_AVAILABLE(HttpStatus.OK, "사용 가능한 이메일입니다."),

    // 닉네임 중복 검사 성공
    NICKNAME_AVAILABLE(HttpStatus.OK, "사용 가능한 닉네임입니다."),

    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),

    TOKEN_REFRESHED(HttpStatus.OK, "토큰이 갱신되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return "[AUTH] " + message;
    }
}
