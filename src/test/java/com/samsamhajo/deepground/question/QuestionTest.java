package com.samsamhajo.deepground;

import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateRequestDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateResponseDto;
import com.samsamhajo.deepground.qna.question.entity.Question;
import com.samsamhajo.deepground.qna.question.entity.QuestionTag;
import com.samsamhajo.deepground.qna.question.exception.QuestionException;
import com.samsamhajo.deepground.qna.question.repository.QuestionRepository;
import com.samsamhajo.deepground.qna.question.repository.QuestionTagRepository;
import com.samsamhajo.deepground.qna.question.service.QuestionService;
import com.samsamhajo.deepground.techStack.entity.TechStack;
import com.samsamhajo.deepground.techStack.repository.TechStackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class QuestionTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TechStackRepository techStackRepository;

    @Autowired
    private QuestionTagRepository questionTagRepository;

    private Long memberId;

    @BeforeEach
    void 테스트용_멤버_저장() {
        // 테스트용 멤버 저장
        Member member = Member.createLocalMember("test@naver.com", "password123", "tester");
        memberRepository.save(member);
        memberId = member.getId();
    }

    @Test
    @DisplayName("질문 생성 통합 테스트 - 성공")
    void createQuestion_success() throws Exception {
        // given
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

        QuestionCreateRequestDto questionCreateRequestDto = new QuestionCreateRequestDto(title, content, techStackNames, mediaFiles);

        QuestionCreateResponseDto questionCreateResponseDto = questionService.createQuestion(questionCreateRequestDto, memberId);

        // assert DB에 잘 들어갔는지 확인
        assertThat(questionRepository.findById(questionCreateResponseDto.getQuestionId())).isPresent();
        assertThat(questionRepository.findById(questionCreateResponseDto.getQuestionId()).get().getTitle()).isEqualTo(title);

        // 해당 질문에 연결된 QuestionTag가 잘 저장되었는지 확인
        List<QuestionTag> questionTags = questionTagRepository.findAllByQuestionId(questionCreateResponseDto.getQuestionId());
        assertThat(questionTags).hasSize(techStackNames.size());

        // 저장된 태그 이름 확인
        List<String> savedTagNames = questionTags.stream()
                .map(qt -> qt.getTechStack().getName())
                .toList();
        assertThat(savedTagNames).containsExactlyInAnyOrderElementsOf(techStackNames);
    }

    @Test
    @DisplayName("질문 생성 성공 서비스 단위 테스트")
    void QuestionServiceTest() throws Exception {

        String title = "테스트";
        String content = "테스트1";

        List<MultipartFile> mediaFiles = List.of(
                new MockMultipartFile("mediaFiles", "image1.png", MediaType.IMAGE_PNG_VALUE, "dummy image content 1".getBytes())
        );


        List<String> techStackNames = List.of("techStack1", "techStack2");
        List<String> categoryNames = List.of("category1", "category2");
        List<TechStack> techStacks = techStackNames.stream()
                .map(name -> TechStack.of(name, categoryNames.toString())) // 정적 팩토리 메서드가 없다면 new TechStack(name) 사용
                .collect(Collectors.toList());
        List<TechStack> savedTechStacks = techStackRepository.saveAll(techStacks);

        QuestionCreateRequestDto questionCreateRequestDto = new QuestionCreateRequestDto(title, content, techStackNames, mediaFiles);

        QuestionCreateResponseDto questionCreateResponseDto = questionService.createQuestion(questionCreateRequestDto, memberId);

        assertThat(questionRepository.findById(questionCreateResponseDto.getQuestionId())).isPresent();
        assertThat(questionRepository.findById(questionCreateResponseDto.getQuestionId()).get().getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("질문 저장 및 조회 테스트")
    void QuestionRepositoryTest ()throws Exception {

        //given
        Question question = Question.of("테스트 제목", "테스트 내용", null);

        //when
        Question saved = questionRepository.save(question);
        Question found = questionRepository.findById(saved.getId()).orElseThrow();

        //then
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName(("제목 없을 시 예외처리"))
    void QuestionTitle() throws Exception {

        String title = "";
        String content = "테스트1";

        List<MultipartFile> mediaFiles = List.of(
                new MockMultipartFile("mediaFiles", "image1.png", MediaType.IMAGE_PNG_VALUE, "dummy image content 1".getBytes())
        );

        // techStack 예시 (빈 리스트 혹은 테스트용 값 넣기)
        List<String> techStackNames = List.of("techStack1", "techStack2");
        List<String> categoryNames = List.of("category1", "category2");
        List<TechStack> techStacks = techStackNames.stream()
                .map(name -> TechStack.of(name, categoryNames.toString())) // 정적 팩토리 메서드가 없다면 new TechStack(name) 사용
                .collect(Collectors.toList());
        List<TechStack> savedTechStacks = techStackRepository.saveAll(techStacks);

        QuestionCreateRequestDto questionCreateRequestDto = new QuestionCreateRequestDto(title, content, techStackNames, mediaFiles);

        assertThatThrownBy(() -> questionService.createQuestion(questionCreateRequestDto, memberId)
        ).isInstanceOf(QuestionException.class)
                .hasMessageContaining("제목을 찾을 수 없습니다.");

    }

    @Test
    @DisplayName(("내용 없을 시 예외처리"))
    void QuestionContent() throws Exception {

        String title = "title";
        String content = "";

        List<MultipartFile> mediaFiles = List.of(
                new MockMultipartFile("mediaFiles", "image1.png", MediaType.IMAGE_PNG_VALUE, "dummy image content 1".getBytes())
        );

        List<String> techStackNames = List.of("techStack1", "techStack2");
        List<String> categoryNames = List.of("category1", "category2");
        List<TechStack> techStacks = techStackNames.stream()
                .map(name -> TechStack.of(name, categoryNames.toString())) // 정적 팩토리 메서드가 없다면 new TechStack(name) 사용
                .collect(Collectors.toList());
        List<TechStack> savedTechStacks = techStackRepository.saveAll(techStacks);

        QuestionCreateRequestDto questionCreateRequestDto = new QuestionCreateRequestDto(title, content, techStackNames, mediaFiles);

        assertThatThrownBy(() -> questionService.createQuestion(questionCreateRequestDto, memberId)
        ).isInstanceOf(QuestionException.class)
                .hasMessageContaining("내용을 찾을 수 없습니다.");

    }

}
