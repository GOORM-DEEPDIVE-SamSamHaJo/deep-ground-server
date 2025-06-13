package com.samsamhajo.deepground.qna.question.Dto;

import com.samsamhajo.deepground.qna.question.entity.QuestionStatus;
import lombok.Getter;

@Getter

public class QuestionUpdateStatusResponseDto {

    private Long questionId;
    private QuestionStatus status;
    private Long memberId;


   public QuestionUpdateStatusResponseDto(Long questionId, QuestionStatus status, Long memberId) {
       this.questionId = questionId;
       this.status = status;
       this.memberId = memberId;
   }
   public static QuestionUpdateStatusResponseDto of(Long questionId, QuestionStatus status, Long memberId) {
       return new QuestionUpdateStatusResponseDto(questionId, status, memberId);
   }
}