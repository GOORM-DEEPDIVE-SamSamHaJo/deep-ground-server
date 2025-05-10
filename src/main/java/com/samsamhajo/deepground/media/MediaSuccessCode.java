package com.samsamhajo.deepground.media;

import com.samsamhajo.deepground.global.success.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MediaSuccessCode implements SuccessCode {
    MEDIA_FOUND(HttpStatus.OK, "미디어 불러오기에 성공했습니다."),

    ;

    private final HttpStatus status;
    private final String message;

    MediaSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}