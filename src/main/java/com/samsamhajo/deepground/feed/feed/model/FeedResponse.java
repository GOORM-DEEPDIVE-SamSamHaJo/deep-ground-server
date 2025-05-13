package com.samsamhajo.deepground.feed.feed.model;// FeedResponse.java

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class FeedResponse {
    private Long id;
    private String content;
    private String memberName;
    private LocalDateTime createdAt;
    private List<Long> imageIds;

    private FeedResponse(Long id, String content, String memberName, LocalDateTime createdAt, List<Long> imageIds) {
        this.id = id;
        this.content = content;
        this.memberName = memberName;
        this.createdAt = createdAt;
        this.imageIds = imageIds;
    }

    public static FeedResponse of(Long id, String content, String memberName, LocalDateTime createdAt, List<Long> imageIds) {
        return new FeedResponse(id, content, memberName, createdAt, imageIds);
    }

}