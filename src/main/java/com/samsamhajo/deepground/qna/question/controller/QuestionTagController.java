package com.samsamhajo.deepground.qna.question.controller;

import com.samsamhajo.deepground.global.success.SuccessResponse;
import com.samsamhajo.deepground.qna.question.Dto.QuestionTagResponseDto;
import com.samsamhajo.deepground.qna.question.exception.QuestionSuccessCode;
import com.samsamhajo.deepground.qna.question.service.QuestionTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question/tag")
public class QuestionTagController {

    private final QuestionTagService questionTagService;

    @GetMapping("/{techStackId}")
    public ResponseEntity<SuccessResponse> getQuestionsByTechStackId(@PathVariable Long techStackId) {

        List<QuestionTagResponseDto> result = questionTagService.getQuestionsByTechStackId(techStackId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(QuestionSuccessCode.QUESTION_TAG, result));
    }
}
