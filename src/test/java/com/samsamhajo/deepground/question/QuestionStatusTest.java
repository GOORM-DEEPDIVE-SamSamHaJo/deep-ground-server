package com.samsamhajo.deepground.question;


import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateRequestDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateResponseDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionUpdateStatusRequestDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionUpdateStatusResponseDto;
import com.samsamhajo.deepground.qna.question.entity.Question;
import com.samsamhajo.deepground.qna.question.entity.QuestionStatus;
import com.samsamhajo.deepground.qna.question.exception.QuestionErrorCode;
import com.samsamhajo.deepground.qna.question.exception.QuestionException;
import com.samsamhajo.deepground.qna.question.repository.QuestionRepository;
import com.samsamhajo.deepground.qna.question.service.QuestionService;
import com.samsamhajo.deepground.techStack.entity.TechStack;
import com.samsamhajo.deepground.techStack.repository.TechStackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class QuestionStatusTest {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private TechStackRepository techStackRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private QuestionRepository questionRepository;
    private Long memberId;

    @BeforeEach
    void 테스트용_멤버_저장() {
        // 테스트용 멤버 저장
        Member member = Member.createLocalMember("test@naver.com", "password123", "tester");
        memberRepository.save(member);
        memberId = member.getId();
    }

    @Test
    @DisplayName("질문 상태 변경")
    public void QuestionStatus_Test(){
        String title = "통합 테스트 질문 제목";
        String content = "이것은 질문 내용입니다.";

        List<MultipartFile> mediaFiles = List.of(
                new MockMultipartFile("mediaFiles", "image1.png", MediaType.IMAGE_PNG_VALUE, "dummy image content 1".getBytes())
        );

        List<String> techStackNames = List.of("techStack1", "techStack2");
        List<String> categoryNames = List.of("category1", "category2");
        List<TechStack> techStacks = techStackNames.stream()
                .map(name -> TechStack.of(name, categoryNames.toString())) // 정적 팩토리 메서드가 없다면 new TechStack(name) 사용
                .collect(Collectors.toList());
        List<TechStack> savedTechStacks = techStackRepository.saveAll(techStacks);


        QuestionCreateRequestDto questionCreateRequestDto =
                new QuestionCreateRequestDto(title, content, techStackNames, mediaFiles);
        QuestionCreateResponseDto questionCreateResponseDto =
                questionService.createQuestion(questionCreateRequestDto, memberId);
        //1.OPEN 확인
        Question question = questionRepository.findById(questionCreateResponseDto.getQuestionId())
                .orElseThrow(() -> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));
        assertThat(question.getQuestionStatus()).isEqualTo(QuestionStatus.OPEN);


        QuestionUpdateStatusRequestDto questionUpdateStatusRequestDto =
                new QuestionUpdateStatusRequestDto(
                        questionCreateResponseDto.getQuestionId(),
                        QuestionStatus.CLOSED,
                        questionCreateResponseDto.getMemberId()
                );


        QuestionUpdateStatusResponseDto questionUpdateStatusResponseDto =
                questionService.updateQuestionStatus(questionUpdateStatusRequestDto, memberId);

       
        assertThat(questionUpdateStatusRequestDto.getStatus()).isEqualTo(QuestionStatus.CLOSED);

        //2.수정확인
        assertThat(questionUpdateStatusResponseDto.getStatus()).isEqualTo(QuestionStatus.CLOSED);





    }
}
