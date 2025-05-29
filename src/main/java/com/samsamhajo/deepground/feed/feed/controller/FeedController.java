package com.samsamhajo.deepground.feed.feed.controller;

import com.samsamhajo.deepground.feed.feed.entity.Feed;
import com.samsamhajo.deepground.feed.feed.exception.FeedSuccessCode;
import com.samsamhajo.deepground.feed.feed.model.FeedCreateRequest;
import com.samsamhajo.deepground.feed.feed.model.FeedListResponse;
import com.samsamhajo.deepground.feed.feed.model.FeedUpdateRequest;
import com.samsamhajo.deepground.feed.feed.service.FeedService;
import com.samsamhajo.deepground.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final Long DEV_MEMBER_ID = 1L;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<Feed>> createFeed(
            @ModelAttribute FeedCreateRequest request) {

        feedService.createFeed(request, DEV_MEMBER_ID);

        return ResponseEntity
                .ok(SuccessResponse.of(FeedSuccessCode.FEED_CREATED));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<FeedListResponse>> getFeeds(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        FeedListResponse feeds = feedService.getFeeds(pageable);

        return ResponseEntity
                .ok(SuccessResponse.of(FeedSuccessCode.FEEDS_RETRIEVED, feeds));
    }
    
    @PutMapping(value = "/{feedId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<Feed>> updateFeed(
            @PathVariable("feedId") Long feedId,
            @ModelAttribute FeedUpdateRequest request) {
        feedService.updateFeed(feedId, request, DEV_MEMBER_ID);
        
        return ResponseEntity
                .ok(SuccessResponse.of(FeedSuccessCode.FEED_UPDATED));
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<SuccessResponse<Void>> deleteFeed(
            @PathVariable("feedId") Long feedId) {
        feedService.deleteFeed(feedId, DEV_MEMBER_ID);

        return ResponseEntity
                .ok(SuccessResponse.of(FeedSuccessCode.FEED_DELETED));
    }
}