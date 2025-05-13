package com.samsamhajo.deepground.media;

import com.samsamhajo.deepground.global.error.core.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MediaErrorCode implements ErrorCode {
    MEDIA_NOT_FOUND(HttpStatus.NOT_FOUND, "미디어를 찾을 수 없습니다."),
    MEDIA_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "미디어 저장에 실패했습니다."),

    ;

    private final HttpStatus status;
    private final String message;

    MediaErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}