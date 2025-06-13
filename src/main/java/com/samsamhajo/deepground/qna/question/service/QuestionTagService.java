package com.samsamhajo.deepground.qna.question.service;

import com.samsamhajo.deepground.qna.question.Dto.QuestionTagResponseDto;
import com.samsamhajo.deepground.qna.question.entity.Question;
import com.samsamhajo.deepground.qna.question.entity.QuestionTag;
import com.samsamhajo.deepground.qna.question.repository.QuestionTagRepository;
import com.samsamhajo.deepground.techStack.entity.TechStack;
import com.samsamhajo.deepground.techStack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionTagService {

    private final QuestionTagRepository questionTagRepository;
    private final TechStackRepository techStackRepository;

    @Transactional
    public void createQuestionTag(Question question, List<String> techStacks) {
        List<QuestionTag> questionTags = techStacks.stream()
                .map(name -> {
                    TechStack techStack = techStackRepository.findByName(name)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그: " + name));
                    return QuestionTag.of(question, techStack);
                })
                .collect(Collectors.toList());

        questionTagRepository.saveAll(questionTags);
    }

    @Transactional(readOnly = true)
    public List<QuestionTagResponseDto> getQuestionsByTechStackId(Long techStackId) {
        List<QuestionTag> tags = questionTagRepository.findByTechStackId(techStackId);

        return tags.stream()
                .map(tag -> new QuestionTagResponseDto(
                        tag.getQuestion().getId(),
                        tag.getQuestion().getTitle(),
                        tag.getQuestion().getContent(),
                        tag.getTechStack().getName()
                ))
                .toList();
    }

}
