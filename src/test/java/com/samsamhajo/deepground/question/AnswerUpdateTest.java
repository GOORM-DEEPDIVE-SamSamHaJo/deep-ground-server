package com.samsamhajo.deepground.question;

import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import com.samsamhajo.deepground.qna.answer.dto.AnswerCreateRequestDto;
import com.samsamhajo.deepground.qna.answer.dto.AnswerCreateResponseDto;
import com.samsamhajo.deepground.qna.answer.dto.AnswerUpdateRequestDto;
import com.samsamhajo.deepground.qna.answer.dto.AnswerUpdateResponseDto;
import com.samsamhajo.deepground.qna.answer.exception.AnswerErrorCode;
import com.samsamhajo.deepground.qna.answer.exception.AnswerException;
import com.samsamhajo.deepground.qna.answer.repository.AnswerRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class AnswerUpdateTest {

    @Autowired
    private AnswerService answerService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TechStackRepository techStackRepository;
    private Long memberId;

    @BeforeEach
    public void setUp() {
        Member member = Member.createLocalMember("ds@gmail.com", "test", "test");
        memberRepository.save(member);
        memberId = member.getId();
    }

    @Test
    @DisplayName("답변 수정 테스트")
    void updateAnswerTest() {

        String title = "테스트";
        String content = "테스트1";
        String answerContent = "test answercontent";
        String UpdateContent = "test updatecontent";
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
        Long test1 = questionCreateResponseDto.getQuestionId();

        //답변 생성
        AnswerCreateRequestDto answerCreateRequestDto = new AnswerCreateRequestDto(answerContent, mediaFiles, test1);
        AnswerCreateResponseDto answerCreateResponseDto = answerService.createAnswer(answerCreateRequestDto, memberId);
        Long test2 = answerCreateResponseDto.getAnswerId();
        System.out.println(answerCreateResponseDto.getAnswerContent());
        String UpdateBeforeContent = answerRepository.findById(test2).get().getAnswerContent();

        //수정
        AnswerUpdateRequestDto answerUpdateRequestDto = new AnswerUpdateRequestDto(UpdateContent, mediaFiles, test1, test2);
        AnswerUpdateResponseDto answerUpdateResponseDto = answerService.updateAnswer(answerUpdateRequestDto, memberId);
        System.out.println(answerUpdateResponseDto.getAnswerContent());
        String UpdateAfterContent = answerRepository.findById(test2).get().getAnswerContent();

        //수정 전, 후 컨텐츠 결과 비교
        assertThat(UpdateAfterContent).isNotEqualTo(UpdateBeforeContent);

    }

    @Test
    @DisplayName("Content Valid 테스트")
    void updateContentValidTest() {
        String title = "테스트";
        String content = "테스트1";
        String answerContent = "test answercontent";
        String UpdateContent = "";
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
        Long test1 = questionCreateResponseDto.getQuestionId();

        //답변 생성
        AnswerCreateRequestDto answerCreateRequestDto = new AnswerCreateRequestDto(answerContent, mediaFiles, test1);
        AnswerCreateResponseDto answerCreateResponseDto = answerService.createAnswer(answerCreateRequestDto, memberId);
        Long test2 = answerCreateResponseDto.getAnswerId();
        System.out.println(answerCreateResponseDto.getAnswerContent());

        //답변 빈칸일시 예외발생
        AnswerUpdateRequestDto answerUpdateRequestDto = new AnswerUpdateRequestDto(UpdateContent, mediaFiles, test1, test2);
        AnswerException answerException = assertThrows(AnswerException.class, () -> answerService.updateAnswer(answerUpdateRequestDto, memberId));
        assertThat(answerException.getMessage()).isEqualTo(AnswerErrorCode.ANSWER_CONTENT_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("답변 없을 시 예외처리")
    void AnswerNotFoundTest() {

        String title = "테스트";
        String content = "테스트1";
        String answerContent = "test answercontent";
        String UpdateContent = "";
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
        Long test1 = questionCreateResponseDto.getQuestionId();

        //답변 생성
        AnswerCreateRequestDto answerCreateRequestDto = new AnswerCreateRequestDto(answerContent, mediaFiles, test1);
        AnswerCreateResponseDto answerCreateResponseDto = answerService.createAnswer(answerCreateRequestDto, memberId);
        Long test2 = 2L;

        AnswerUpdateRequestDto answerUpdateRequestDto = new AnswerUpdateRequestDto(UpdateContent, mediaFiles, test1, test2);

        AnswerException answerException = assertThrows(AnswerException.class, () -> answerService.updateAnswer(answerUpdateRequestDto, memberId));

        assertThat(answerException.getMessage()).isEqualTo(AnswerErrorCode.ANSWER_NOT_FOUND.getMessage());
    }
}
