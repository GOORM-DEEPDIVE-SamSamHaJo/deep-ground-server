package com.samsamhajo.deepground.qna.answer.service;

import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import com.samsamhajo.deepground.qna.answer.dto.*;
import com.samsamhajo.deepground.qna.answer.entity.Answer;
import com.samsamhajo.deepground.qna.answer.entity.AnswerMedia;
import com.samsamhajo.deepground.qna.answer.repository.AnswerMediaRepository;
import com.samsamhajo.deepground.qna.answer.repository.AnswerRepository;
import com.samsamhajo.deepground.qna.answer.exception.AnswerErrorCode;
import com.samsamhajo.deepground.qna.answer.exception.AnswerException;
import com.samsamhajo.deepground.qna.question.entity.Question;
import com.samsamhajo.deepground.qna.question.entity.QuestionMedia;
import com.samsamhajo.deepground.qna.question.exception.QuestionErrorCode;
import com.samsamhajo.deepground.qna.question.exception.QuestionException;
import com.samsamhajo.deepground.qna.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final AnswerMediaService answerMediaService;
    private final AnswerLikeService answerLikeService;
    private final AnswerMediaRepository answerMediaRepository;


    @Transactional
    public AnswerCreateResponseDto createAnswer(AnswerCreateRequestDto answerCreateRequestDto, Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Question question = questionRepository.findById(answerCreateRequestDto.getQuestionId())
                .orElseThrow(() -> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));

        if(!StringUtils.hasText(answerCreateRequestDto.getAnswerContent())) {
            throw new AnswerException(AnswerErrorCode.ANSWER_CONTENT_REQUIRED);
        }

        Answer answer = Answer.of(
                answerCreateRequestDto.getAnswerContent(),
                member,
                question
        );

        Answer saved = answerRepository.save(answer);

        question.incrementAnswerCount();
        createAnswerMedia(answerCreateRequestDto, saved);

        return AnswerCreateResponseDto.of(
                saved.getAnswerContent(),
                saved.getQuestion().getId(),
                saved.getMember().getId(),
                saved.getId()
        );
    }

    @Transactional
    public Long deleteAnswer(Long answerId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(()->
                new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerException(AnswerErrorCode.ANSWER_NOT_FOUND));

        if (!answer.getMember().getId().equals(memberId)) {
            throw new AnswerException(AnswerErrorCode.ANSWER_MEMBER_MISMTACH);
        }
        Question question = questionRepository.findById(answer.getQuestion().getId())
                .orElseThrow(() -> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));

        answerMediaService.deleteAnswerMedia(answerId);
        answerLikeService.deleteAllByAnswerId(answerId);
        answerRepository.deleteById(answer.getId());
        question.decrementAnswerCount();

        return answer.getId();
    }

    @Transactional
    public AnswerUpdateResponseDto updateAnswer(AnswerUpdateRequestDto answerUpdateRequestDto, Long memberId) {


        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Question question = questionRepository.findById(answerUpdateRequestDto.getQuestionId())
                .orElseThrow(() -> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));

        Answer answer = answerRepository.findById(answerUpdateRequestDto.getAnswerId())
                .orElseThrow(()-> new AnswerException(AnswerErrorCode.ANSWER_NOT_FOUND));

        if(!StringUtils.hasText(answerUpdateRequestDto.getAnswerContent())) {
            throw new AnswerException(AnswerErrorCode.ANSWER_CONTENT_REQUIRED);
        }

        if(!answer.getMember().getId().equals(memberId)) {
            throw new AnswerException(AnswerErrorCode.ANSWER_MEMBER_MISMTACH);
        }

        answer.updateAnswer(answerUpdateRequestDto.getAnswerContent());

        answerMediaService.deleteAnswerMedia(answer.getId());
        updateAnswerMedia(answerUpdateRequestDto, answer);

        return AnswerUpdateResponseDto.of(
                answer.getAnswerContent(),
                answer.getQuestion().getId(),
                answer.getId(),
                member.getId()
        );

    }
    private void createAnswerMedia(AnswerCreateRequestDto answerCreateRequestDto, Answer answer) {
        answerMediaService.createAnswerMedia(answer, answerCreateRequestDto.getMediaFiles());
    }

    private void updateAnswerMedia(AnswerUpdateRequestDto answerUpdateRequestDto, Answer answer) {
        answerMediaService.createAnswerMedia(answer, answerUpdateRequestDto.getMediaFiles());
    }

    public int countAnswersByQuestionId(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(()-> {
            throw new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND);
        });
        return question.getAnswerCount();
    }

    public List<AnswerCreateResponseDto> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers = answerRepository.findAllByQuestionId(questionId);
        return answers.stream()
                .map(answer -> new AnswerCreateResponseDto(
                        answer.getAnswerContent(),
                        answer.getQuestion().getId(),
                        answer.getMember().getId(),
                        answer.getId()

                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AnswerDetailDto getAnswer(Long answerId, Long memberId) {

        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new AnswerException(AnswerErrorCode.ANSWER_NOT_FOUND));
        List<AnswerMedia> answerMedia = answerMediaRepository.findAllByAnswerId(answerId);
        List<String> imageUrls = answerMedia.stream()
                .map(AnswerMedia::getAnswerCommentUrl)
                .collect(Collectors.toList());

        return AnswerDetailDto.of(
                answer.getAnswerContent(),
                answer.getQuestion().getId(),
                answer.getMember().getId(),
                answerId,
                imageUrls
        );

    }
}
