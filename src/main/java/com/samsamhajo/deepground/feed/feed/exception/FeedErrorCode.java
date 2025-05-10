package com.samsamhajo.deepground.feed.feed.exception;

import com.samsamhajo.deepground.global.error.core.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FeedErrorCode implements ErrorCode {
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "피드를 찾을 수 없습니다."),
    INVALID_FEED_CONTENT(HttpStatus.BAD_REQUEST, "피드 내용이 유효하지 않습니다."),

    ;

    private final HttpStatus status;
    private final String message;

    FeedErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}