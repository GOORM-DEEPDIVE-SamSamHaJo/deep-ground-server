package com.samsamhajo.deepground;

import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import com.samsamhajo.deepground.qna.answer.dto.AnswerCreateRequestDto;
import com.samsamhajo.deepground.qna.answer.dto.AnswerCreateResponseDto;
import com.samsamhajo.deepground.qna.answer.service.AnswerService;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateRequestDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateResponseDto;
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
public class AnswerTest {

    @Autowired
    private MemberRepository memberRepository;

    private Long memberId;
    private Long questionId;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TechStackRepository techStackRepository;

    @BeforeEach
    void 테스트용_멤버_저장() {
        // 테스트용 멤버 저장
        Member member = Member.createLocalMember("test@naver.com", "password123", "tester");
        memberRepository.save(member);
        memberId = member.getId();

    }

    @Test
    @DisplayName("댓글 생성 Service 테스트 코드")
    public void testAnswerService()     {

        String title = "테스트";
        String content = "테스트1";

        String answerContent = "test answercontent";

        List<MultipartFile> mediaFiles = List.of(
                new MockMultipartFile("mediaFiles", "image1.png", MediaType.IMAGE_PNG_VALUE, "dummy image content 1".getBytes())
        );

        //질문 생성
        List<String> techStackNames = List.of("techStack1", "techStack2");
        List<String> categoryNames = List.of("category1", "category2");
        List<TechStack> techStacks = techStackNames.stream()
                .map(name -> TechStack.of(name, categoryNames.toString())) // 정적 팩토리 메서드가 없다면 new TechStack(name) 사용
                .collect(Collectors.toList());
        List<TechStack> savedTechStacks = techStackRepository.saveAll(techStacks);

        QuestionCreateRequestDto questionCreateRequestDto = new QuestionCreateRequestDto(title, content, techStackNames, mediaFiles);

        QuestionCreateResponseDto questionCreateResponseDto = questionService.createQuestion(questionCreateRequestDto, memberId);
        questionId = questionCreateResponseDto.getQuestionId();

        //후에 답변 작성
        AnswerCreateRequestDto answerCreateRequestDto = new AnswerCreateRequestDto(answerContent, mediaFiles, questionId);
        AnswerCreateResponseDto answerCreateResponseDto = answerService.createAnswer(answerCreateRequestDto, memberId);

        //생성된 questionId, Answer에 넘어간 questionId가 같은지 확인
        assertThat(answerCreateResponseDto.getQuestionId()).isEqualTo(questionCreateResponseDto.getQuestionId());
        //우리가 작성한 내용이 잘 들어갔는지
        assertThat(answerCreateResponseDto.getAnswerContent()).isEqualTo(answerContent);

    }

}