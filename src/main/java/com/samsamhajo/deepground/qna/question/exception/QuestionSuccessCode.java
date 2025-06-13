package com.samsamhajo.deepground.qna.question.exception;

import com.samsamhajo.deepground.global.success.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum QuestionSuccessCode implements SuccessCode {

    QUESTION_CREATED(HttpStatus.CREATED, "질문이 성공적으로 생성되었습니다."),
    QUESTION_DELETED(HttpStatus.OK, "질문이 성공적으로 삭제되었습니다."),
    QUESTION_UPDATED(HttpStatus.OK, "질문이 성공적으로 업데이트 되었습니다."),
    QUESTION_MEDIA_UPDATED(HttpStatus.OK, "질문 미디어가 성공적으로 수정되었습니다."),
    QUESTION_TAG(HttpStatus.OK, "질문 태그가 성공적으로 조회되었습니다."),
    QUESTION_STATUS_UPDATED(HttpStatus.OK, "질문 상태가 성공적으로 수정되었습니다.");


    private final HttpStatus status;
    private final String message;

    QuestionSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}