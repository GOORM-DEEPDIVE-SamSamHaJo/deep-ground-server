package com.samsamhajo.deepground.qna.answer.controller;

import com.samsamhajo.deepground.global.success.SuccessResponse;
import com.samsamhajo.deepground.qna.answer.dto.AnswerCreateRequestDto;
import com.samsamhajo.deepground.qna.answer.dto.AnswerCreateResponseDto;
import com.samsamhajo.deepground.qna.answer.dto.AnswerUpdateRequestDto;
import com.samsamhajo.deepground.qna.answer.dto.AnswerUpdateResponseDto;
import com.samsamhajo.deepground.qna.answer.service.AnswerService;
import com.samsamhajo.deepground.qna.answer.exception.AnswerSuccessCode;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse> createAnswer  (
            @Valid @RequestBody AnswerCreateRequestDto answerCreateRequestDto
    )
    {
        Long memberId = 1L; // 테스트용 memberId
        AnswerCreateResponseDto answerCreateResponseDto = answerService.createAnswer(answerCreateRequestDto, memberId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.of(AnswerSuccessCode.ANSWER_CREATED, answerCreateResponseDto));

    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<SuccessResponse> deleteAnswer(
            @PathVariable Long answerId){

                Long memberId = 1L;
                Long questionId = 1L;

                answerService.deleteAnswer(answerId, memberId,questionId);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(SuccessResponse.of(AnswerSuccessCode.ANSWER_DELETED));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<AnswerUpdateResponseDto>> updateAnswer(
            @Valid @ModelAttribute AnswerUpdateRequestDto answerUpdateRequestDto
    ) {
        Long memberId = 1L; //테스트용 memberId
        AnswerUpdateResponseDto answerUpdateResponseDto = answerService.updateAnswer(answerUpdateRequestDto, memberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(AnswerSuccessCode.ANSWER_UPDATED, answerUpdateResponseDto));

    }

}


