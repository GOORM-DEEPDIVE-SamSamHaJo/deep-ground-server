package com.samsamhajo.deepground.question;

import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import com.samsamhajo.deepground.qna.answer.dto.AnswerCreateRequestDto;
import com.samsamhajo.deepground.qna.answer.dto.AnswerCreateResponseDto;
import com.samsamhajo.deepground.qna.answer.repository.AnswerLikeRepository;
import com.samsamhajo.deepground.qna.answer.service.AnswerLikeService;
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
public class AnswerLikeTest {

    @Autowired
    private MemberRepository memberRepository;
    private Long memberId;
    private Long questionId;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerLikeRepository answerLikeRepository;
    @Autowired
    private AnswerLikeService answerLikeService;
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
        Long answerId = answerCreateResponseDto.getAnswerId();

        //좋아요 작성
        answerLikeService.answerLike(answerId, memberId);

        //answerRepository에 제대로 들어갔는지 확인
        assertThat(answerLikeRepository.existsByAnswerIdAndMemberId(answerId, memberId)).isTrue();
        assertThat(answerLikeRepository.countByAnswerId(answerId)).isEqualTo(1);

        //잘 삭제되었는지 확인
        answerLikeService.answerUnLike(answerId, memberId);
        assertThat(answerLikeRepository.existsByAnswerIdAndMemberId(answerId, memberId)).isFalse();
        assertThat(answerLikeRepository.countByAnswerId(answerId)).isEqualTo(0);




    }
}
