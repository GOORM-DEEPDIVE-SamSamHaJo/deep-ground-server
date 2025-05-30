package com.samsamhajo.deepground.qna.question.controller;

import com.samsamhajo.deepground.global.success.SuccessResponse;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateRequestDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionUpdateResponseDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionUpdateRequestDto;
import com.samsamhajo.deepground.qna.question.entity.Question;
import com.samsamhajo.deepground.qna.question.exception.QuestionSuccessCode;
import com.samsamhajo.deepground.qna.question.repository.QuestionMediaRepository;
import com.samsamhajo.deepground.qna.question.repository.QuestionRepository;
import com.samsamhajo.deepground.qna.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final QuestionMediaRepository questionMediaRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<Question>> createQuestion(
            @Valid @ModelAttribute QuestionCreateRequestDto questionCreateRequestDto,
            @RequestParam Long memberId) {

        Question question = questionService.createQuestion(questionCreateRequestDto, memberId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.of(QuestionSuccessCode.QUESTION_CREATED, question));

    }

    @DeleteMapping("/{questionId}/delete")
    public ResponseEntity<SuccessResponse> deleteQuestion(
            @PathVariable Long questionId
            ,       @RequestParam Long memberId) {

        questionService.deleteQuestion(questionId, memberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(QuestionSuccessCode.QUESTION_DELETED, questionId));

    }


    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse> updateQuestion(
            @Valid @ModelAttribute QuestionUpdateRequestDto questionUpdateRequestDto,
            @RequestParam Long memberId) {

        QuestionUpdateResponseDto questionUpdateResponseDto = questionService.updateQuestion(questionUpdateRequestDto, memberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(QuestionSuccessCode.QUESTION_UPDATED, questionUpdateResponseDto));
    }


}