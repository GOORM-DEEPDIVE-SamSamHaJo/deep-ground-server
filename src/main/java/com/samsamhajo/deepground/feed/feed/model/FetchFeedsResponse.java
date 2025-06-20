package com.samsamhajo.deepground.feed.feed.model;

import lombok.Getter;

import java.util.List;

@Getter
public class FetchFeedsResponse {
    private List<FetchFeedResponse> feeds;
    private long total;
    private int page;
    private int pageSize;
    private int totalPages;

    private FetchFeedsResponse(List<FetchFeedResponse> feeds) {
        this.feeds = feeds;
    }

    private FetchFeedsResponse(List<FetchFeedResponse> feeds, long total, int page, int pageSize, int totalPages) {
        this.feeds = feeds;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    public static FetchFeedsResponse of(List<FetchFeedResponse> feeds) {
        return new FetchFeedsResponse(feeds);
    }

    public static FetchFeedsResponse of(List<FetchFeedResponse> feeds, long total, int page, int pageSize, int totalPages) {
        return new FetchFeedsResponse(
                feeds,
                total,
                page,
                pageSize,
                totalPages
        );
    }
} 