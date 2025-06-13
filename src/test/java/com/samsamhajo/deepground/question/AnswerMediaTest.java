package com.samsamhajo.deepground.question;

import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import com.samsamhajo.deepground.qna.answer.dto.AnswerCreateRequestDto;
import com.samsamhajo.deepground.qna.answer.dto.AnswerCreateResponseDto;
import com.samsamhajo.deepground.qna.answer.entity.Answer;
import com.samsamhajo.deepground.qna.answer.repository.AnswerMediaRepository;
import com.samsamhajo.deepground.qna.answer.repository.AnswerRepository;
import com.samsamhajo.deepground.qna.answer.service.AnswerMediaService;
import com.samsamhajo.deepground.qna.answer.service.AnswerService;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateRequestDto;
import com.samsamhajo.deepground.qna.question.Dto.QuestionCreateResponseDto;
import com.samsamhajo.deepground.qna.question.repository.QuestionMediaRepository;
import com.samsamhajo.deepground.qna.question.service.QuestionService;
import com.samsamhajo.deepground.techStack.entity.TechStack;
import com.samsamhajo.deepground.techStack.repository.TechStackRepository;
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

@Transactional
@SpringBootTest
public class AnswerMediaTest {

    @Autowired
    private AnswerService answerService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionMediaRepository questionMediaRepository;
    @Autowired
    private AnswerMediaRepository answerMediaRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AnswerMediaService answerMediaService;
    @Autowired
    private TechStackRepository techStackRepository;
    private Long memberId;

    @Test
    @DisplayName("답변_미디어_생성_후 삭제_확인_테스트")
    void answer_exception() {

        String title = "미디어 생성 테스트";
        String content = "미디어 생성 테스트 답변";
        String answerContent = "답변 테스트";

        Member member = Member.createLocalMember("tses1@gmail.com", "TSE S1", "TSE S2");
        memberRepository.save(member);
        memberId = member.getId();

        List<MultipartFile> mediaFiles = List.of(
                new MockMultipartFile("mediaFiles", "image1.png", MediaType.IMAGE_PNG_VALUE, "dummy image content 1".getBytes())
        );

        //RequestDto에 인자 전달 후, Question 생성
        List<String> techStackNames = List.of("techStack1", "techStack2");
        List<String> categoryNames = List.of("category1", "category2");
        List<TechStack> techStacks = techStackNames.stream()
                .map(name -> TechStack.of(name, categoryNames.toString())) // 정적 팩토리 메서드가 없다면 new TechStack(name) 사용
                .collect(Collectors.toList());
        List<TechStack> savedTechStacks = techStackRepository.saveAll(techStacks);

        QuestionCreateRequestDto questionCreateRequestDto = new QuestionCreateRequestDto(title, content, techStackNames, mediaFiles);

        QuestionCreateResponseDto questionCreateResponseDto = questionService.createQuestion(questionCreateRequestDto, memberId);
        Long test = questionCreateResponseDto.getQuestionId();

        AnswerCreateRequestDto answerCreateRequestDto = new AnswerCreateRequestDto(answerContent, mediaFiles, test);
        AnswerCreateResponseDto answerCreateResponseDto = answerService.createAnswer(answerCreateRequestDto, memberId);

        Long answerId = answerCreateResponseDto.getAnswerId();

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변이 존재하지 않습니다."));

        Long test2 = answer.getId();

        answerMediaService.createAnswerMedia(answer,mediaFiles);
        assertThat(answerMediaRepository.findAllByAnswerId(answer.getId())).isNotEmpty();
        assertThat(answerMediaRepository.findAllByAnswerId(answer.getId()).isEmpty()).isFalse();

        answerMediaService.deleteAnswerMedia(test2);
        assertThat(answerMediaRepository.findAllByAnswerId(answer.getId()).isEmpty()).isTrue();


    }
}

