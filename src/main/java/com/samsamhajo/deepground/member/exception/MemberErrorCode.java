package com.samsamhajo.deepground.member.exception;

import com.samsamhajo.deepground.global.error.core.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    INVALID_MEMBER_EMAIL(HttpStatus.BAD_REQUEST, "찾을 수 없는 사용자입니다"),
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "입력하신 회원 ID가 올바르지 않거나 존재하지 않습니다.");


    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return "[FRIEND ERROR]" + message;
    }
}