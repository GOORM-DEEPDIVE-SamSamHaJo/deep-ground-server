package com.samsamhajo.deepground.feed.feed.model;

import lombok.Getter;

import java.util.List;

@Getter
public class FeedListResponse {
    private List<FeedResponse> feeds;
    private int currentPage;
    private int totalPages;

    private FeedListResponse(List<FeedResponse> feeds, int currentPage, int totalPages) {
        this.feeds = feeds;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public static FeedListResponse of(List<FeedResponse> feeds, int currentPage, int totalPages) {
        return new FeedListResponse(feeds, currentPage, totalPages);
    }
}