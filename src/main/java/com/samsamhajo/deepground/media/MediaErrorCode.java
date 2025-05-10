package com.samsamhajo.deepground.media;

import com.samsamhajo.deepground.global.error.core.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MediaErrorCode implements ErrorCode {
    MEDIA_NOT_FOUND(HttpStatus.NOT_FOUND, "미디어를 찾을 수 없습니다."),

    ;

    private final HttpStatus status;
    private final String message;

    MediaErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}