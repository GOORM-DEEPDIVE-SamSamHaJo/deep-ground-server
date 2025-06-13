package com.samsamhajo.deepground;


import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateRequestDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateResponseDto;
import com.samsamhajo.deepground.qna.question.repository.QuestionRepository;
import com.samsamhajo.deepground.qna.question.service.QuestionService;
import com.samsamhajo.deepground.techStack.entity.TechStack;
import com.samsamhajo.deepground.techStack.repository.TechStackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class QuestionDeleteTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TechStackRepository techStackRepository;

    private Long memberId;

    @BeforeEach
    void 테스트용_멤버_저장() {
        // 테스트용 멤버 저장
        Member member = Member.createLocalMember("test@naver.com", "password123", "tester");
        memberRepository.save(member);
        memberId = member.getId();
    }

    @Test
    @DisplayName("질문 삭제 서비스 테스트")
    public void testDeleteQuestion() {

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
        Long questionId = questionCreateResponseDto.getQuestionId();
        System.out.println(questionId);
        Long deleteId = questionService.deleteQuestion(questionId, memberId);
        System.out.println(deleteId);

        //questionId가 없어야 함
        assertThat(questionRepository.findById(questionId).isPresent()).isFalse();
        //questionId, deleteId가 같아야함
        assertThat(deleteId.equals(questionId)).isTrue();

        //TODO : 미디어 삭제 추가로직 구현


    }

    @Test
    @DisplayName("질문 삭제 후 DB에 반영되는지 테스트")
    public void testDeleteQuestion2() {

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
        Long questionId = questionCreateResponseDto.getQuestionId();
        System.out.println(questionId);

        //질문 생성 후 실제로 DB에 반영됐는지 확인
        assertThat(questionRepository.findById(questionId).get().getTitle()).isNotNull();
        System.out.println(questionRepository.findById(questionId).get().getTitle());

        //삭제
        Long deleteId = questionService.deleteQuestion(questionId, memberId);
        System.out.println(deleteId);

        //questionId, deleteId가 같아야함
        assertThat(deleteId.equals(questionId)).isTrue();

        //삭제 후 DB에서 지워졌는지 확인
        assertThat(questionRepository.findById(questionId).isEmpty()).isTrue();
        System.out.println(questionRepository.findById(questionId).isEmpty());




    }
}
