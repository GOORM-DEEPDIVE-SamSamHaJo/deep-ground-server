package com.samsamhajo.deepground.studyGroup.controller;

import com.samsamhajo.deepground.global.success.SuccessResponse;
import com.samsamhajo.deepground.global.utils.GlobalLogger;
import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.studyGroup.dto.StudyGroupCommentRequest;
import com.samsamhajo.deepground.studyGroup.dto.StudyGroupCommentResponse;
import com.samsamhajo.deepground.studyGroup.service.StudyGroupCommentService;
import com.samsamhajo.deepground.studyGroup.success.StudyGroupSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-group/comments")
public class StudyGroupCommentController {

  private final StudyGroupCommentService commentService;

  @PostMapping
  public ResponseEntity<SuccessResponse<StudyGroupCommentResponse>> writeComment(
      @RequestBody @Valid StudyGroupCommentRequest request,
      @RequestAttribute("member") Member member
  ) {
    GlobalLogger.info("스터디 댓글 작성 요청", member.getEmail(), request.getStudyGroupId());

    StudyGroupCommentResponse response = commentService.writeComment(request, member.getId());

    return ResponseEntity
        .status(StudyGroupSuccessCode.COMMENT_CREATE_SUCCESS.getStatus())
        .body(SuccessResponse.of(StudyGroupSuccessCode.COMMENT_CREATE_SUCCESS, response));
  }
}
