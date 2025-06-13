package com.samsamhajo.deepground.qna.question.exception;

import com.samsamhajo.deepground.global.error.core.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum QuestionErrorCode implements ErrorCode {

    QUESTION_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "제목을 찾을 수 없습니다."),
    QUESTION_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "내용을 찾을 수 없습니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."),
    QUESTION_MEMBER_MISMATCH(HttpStatus.BAD_REQUEST, "질문을 작성한 사용자가 아닙니다.");


    private final HttpStatus status;
    private final String message;

    QuestionErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

