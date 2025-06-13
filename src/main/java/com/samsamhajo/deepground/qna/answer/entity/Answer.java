package com.samsamhajo.deepground.qna.answer.entity;

import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.qna.answer.dto.AnswerUpdateRequestDto;
import com.samsamhajo.deepground.qna.comment.entity.Comment;
import com.samsamhajo.deepground.qna.question.entity.Question;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "answers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id",nullable = false)
    private Long id;

    @Column(name = "answer_content", nullable = false, columnDefinition = "TEXT")
    private String answerContent;

    @Column(name = "answer_like_count",nullable = false)
    private int answerLikeCount = 0;

    @Column(name = "comment_count",nullable = false)
    private int commentCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AnswerMedia> answerMedia = new ArrayList<>();

    private Answer(String answerContent, Member member, Question question) {
        this.answerContent = answerContent;
        this.member = member;
        this.question = question;
    }

    public static Answer of(String answerContent, Member member, Question question) {
        return new Answer(answerContent, member, question);
    }

    public void updateAnswer(String answerContent) {
        this.answerContent = answerContent;
    }

    public void incrementAnswerLikeCount() {
        answerLikeCount++;
    }
    public void decrementAnswerLikeCount() {
        if (answerLikeCount>0) answerLikeCount--;
    }
}
