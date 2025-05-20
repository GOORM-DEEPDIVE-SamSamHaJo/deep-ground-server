package com.samsamhajo.deepground.chat.exception;

import com.samsamhajo.deepground.global.error.core.WebSocketErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatMessageErrorCode implements WebSocketErrorCode {
    INVALID_MESSAGE(4001, "유효하지 않은 메시지입니다."),
    ;

    private final int status;
    private final String message;
}
