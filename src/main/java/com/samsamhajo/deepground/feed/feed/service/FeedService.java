package com.samsamhajo.deepground.feed.feed.service;

import com.samsamhajo.deepground.feed.feed.entity.Feed;
import com.samsamhajo.deepground.feed.feed.exception.FeedErrorCode;
import com.samsamhajo.deepground.feed.feed.exception.FeedException;
import com.samsamhajo.deepground.feed.feed.model.FeedCreateRequest;
import com.samsamhajo.deepground.feed.feed.model.FeedUpdateRequest;
import com.samsamhajo.deepground.feed.feed.model.FetchFeedResponse;
import com.samsamhajo.deepground.feed.feed.model.FetchFeedsResponse;
import com.samsamhajo.deepground.feed.feed.repository.FeedRepository;
import com.samsamhajo.deepground.feed.feedcomment.service.FeedCommentService;
import com.samsamhajo.deepground.feed.feedshared.model.FetchSharedFeedResponse;
import com.samsamhajo.deepground.feed.feedshared.service.SharedFeedService;
import com.samsamhajo.deepground.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedMediaService feedMediaService;
    private final FeedCommentService feedCommentService;
    private final FeedLikeService feedLikeService;
    private final SharedFeedService sharedFeedService;

    @Transactional
    public Feed createFeed(FeedCreateRequest request, Member member) {
        if (!StringUtils.hasText(request.getContent())) {
            throw new FeedException(FeedErrorCode.INVALID_FEED_CONTENT);
        }

        Feed feed = Feed.of(request.getContent(), member);

        feedRepository.save(feed);

        saveFeedMedia(request, feed);

        return feed;
    }

    @Transactional
    public Feed updateFeed(Long feedId, FeedUpdateRequest request, Long memberId) {
        if (!StringUtils.hasText(request.getContent())) {
            throw new FeedException(FeedErrorCode.INVALID_FEED_CONTENT);
        }

        Feed feed = feedRepository.getById(feedId);

        // 피드 내용 업데이트
        feed.updateContent(request.getContent());

        // 미디어 업데이트
        feedMediaService.updateFeedMedia(feed, request);

        return feed;
    }


    public FetchFeedsResponse getFeeds(Pageable pageable, Long memberId) {

        Page<Feed> feeds = feedRepository.findAll(pageable);

        return FetchFeedsResponse.of(
                feeds.getContent().stream()
                        .map(feed -> {
                            FetchSharedFeedResponse sharedFeedResponse =
                                    sharedFeedService.getSharedFeedResponse(feed.getId());

                            return FetchFeedResponse.builder()
                                    .feedId(feed.getId())
                                    .content(feed.getContent())
                                    .createdAt(feed.getCreatedAt().toLocalDate())
                                    .memberId(feed.getMember().getId())
                                    .memberName(feed.getMember().getNickname())
                                    .mediaIds(feedMediaService.findAllMediaIdsByFeedId(feed.getId()))
                                    .shareCount(sharedFeedService.countSharedFeedByOriginFeedId(feed.getId()))
                                    .commentCount(feedCommentService.countFeedCommentsByFeedId(feed.getId()))
                                    .likeCount(feedLikeService.countFeedLikeByFeedId(feed.getId()))
                                    .isLiked(feedLikeService.isLiked(feed.getId(), memberId))
                                    .isShared(sharedFeedResponse != null)
                                    .sharedFeed(sharedFeedResponse)
                                    .build();
                        }
                        ).toList(),
                feeds.getTotalElements(),
                feeds.getNumber(),        // 현재 페이지 번호 (0부터 시작)
                feeds.getSize(),          // 요청된 페이지 크기
                feeds.getTotalPages()
        );
    }

    private void saveFeedMedia(FeedCreateRequest request, Feed feed) {
        feedMediaService.createFeedMedia(feed, request.getImages());
    }

    @Transactional
    public void deleteFeed(Long feedId) {

        deleteRelatedEntities(feedId);

        feedRepository.deleteById(feedId);
    }

    private void deleteRelatedEntities(Long feedId) {
        feedCommentService.deleteFeedCommentByFeed(feedId);
        feedLikeService.deleteAllByFeedId(feedId);
        feedMediaService.deleteAllByFeedId(feedId);
    }
}