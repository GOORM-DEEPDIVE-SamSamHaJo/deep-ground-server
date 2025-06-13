package com.samsamhajo.deepground.qna.question.service;

import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateRequestDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateResponseDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionUpdateResponseDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionUpdateRequestDto;
import com.samsamhajo.deepground.qna.question.entity.Question;
import com.samsamhajo.deepground.qna.question.entity.QuestionTag;
import com.samsamhajo.deepground.qna.question.exception.QuestionErrorCode;
import com.samsamhajo.deepground.qna.question.exception.QuestionException;
import com.samsamhajo.deepground.qna.question.repository.QuestionRepository;
import com.samsamhajo.deepground.qna.question.repository.QuestionTagRepository;
import com.samsamhajo.deepground.techStack.entity.TechStack;
import com.samsamhajo.deepground.techStack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService{

    private final QuestionRepository questionRepository;
    private final QuestionMediaService questionMediaService;
    private final QuestionTagRepository questionTagRepository;
    private final TechStackRepository techStackRepository;
    private final MemberRepository memberRepository;
    private final QuestionTagService questionTagService;

    //질문 생성
    @Transactional
    public QuestionCreateResponseDto createQuestion(QuestionCreateRequestDto questionCreateRequestDto, Long memberId) {
       Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if(!StringUtils.hasText(questionCreateRequestDto.getTitle())) {
            throw new QuestionException(QuestionErrorCode.QUESTION_TITLE_REQUIRED);
        }

        if(!StringUtils.hasText(questionCreateRequestDto.getContent())) {
            throw new QuestionException(QuestionErrorCode.QUESTION_CONTENT_REQUIRED);
        }

        Question question = Question.of(
                questionCreateRequestDto.getTitle(),
                questionCreateRequestDto.getContent(),
                member
        );

        Question saved = questionRepository.save(question);
        createQuestionMedia(questionCreateRequestDto, question);
        questionTagService.createQuestionTag(question, questionCreateRequestDto.getTechStacks());



        return QuestionCreateResponseDto.of(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                memberId,
                questionCreateRequestDto.getTechStacks()
        );
    }

    @Transactional
    public Long deleteQuestion(Long questionId, Long memberId) {

        //TODO : question을 작성한 멤버가 맞는지, 삭제권한 있는지 추후 로직 작성

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));

        questionTagRepository.deleteAllByQuestionId(questionId);
        questionMediaService.deleteQuestionMedia(questionId);
        questionRepository.deleteById(questionId);

        return question.getId();

    }

    @Transactional
    public QuestionUpdateResponseDto updateQuestion(QuestionUpdateRequestDto questionUpdateRequestDto, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if(!StringUtils.hasText(questionUpdateRequestDto.getTitle())) {
            throw new QuestionException(QuestionErrorCode.QUESTION_TITLE_REQUIRED);
        }

        if(!StringUtils.hasText(questionUpdateRequestDto.getContent())) {
            throw new QuestionException(QuestionErrorCode.QUESTION_CONTENT_REQUIRED);
        }

        Question question = questionRepository.findById(questionUpdateRequestDto.getQuestionId())
                .orElseThrow(()-> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));

        questionTagRepository.deleteAllByQuestionId(question.getId());

        List<String> techStacks = questionUpdateRequestDto.getTechStacks();
        for (String name : techStacks) {
            TechStack techStack = techStackRepository.findByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그: " + name));
            QuestionTag questionTag = QuestionTag.of(question, techStack);
            questionTagRepository.save(questionTag);
        }


        QuestionUpdateResponseDto questionUpdateResponseDto = QuestionUpdateResponseDto.of(
                questionUpdateRequestDto.getQuestionId(),
                questionUpdateRequestDto.getTitle(),
                questionUpdateRequestDto.getContent(),
                memberId,
                techStacks
        );

        question.updateQuesiton(questionUpdateRequestDto.getTitle(), questionUpdateRequestDto.getContent());
        questionMediaService.deleteQuestionMedia(question.getId());
        updateQuestionMedia(questionUpdateRequestDto, question);

        return questionUpdateResponseDto;

    }
    private void createQuestionMedia(QuestionCreateRequestDto questionCreateRequestDto, Question question) {
        questionMediaService.createQuestionMedia(question, questionCreateRequestDto.getImages());
    }

    private void updateQuestionMedia(QuestionUpdateRequestDto questionUpdateRequestDto, Question question) {
        questionMediaService.createQuestionMedia(question, questionUpdateRequestDto.getImages());
    }

}
