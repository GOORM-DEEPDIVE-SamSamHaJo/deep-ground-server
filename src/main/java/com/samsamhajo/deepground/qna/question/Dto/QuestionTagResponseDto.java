package com.samsamhajo.deepground.qna.question.Dto;

import lombok.Getter;

@Getter
public class QuestionTagResponseDto {

        private Long questionId;
        private String title;
        private String content;
        private String techStackName;


    public QuestionTagResponseDto(Long questionId, String title, String content, String techStackName) {
        this.questionId = questionId;
        this.title = title;
        this.content = content;
        this.techStackName = techStackName;
    }

    public static QuestionTagResponseDto of(Long questionId, String title, String content, String techStackName) {
        return new QuestionTagResponseDto(questionId, title, content, techStackName);
    }
}
