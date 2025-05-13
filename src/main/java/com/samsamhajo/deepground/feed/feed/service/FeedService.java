package com.samsamhajo.deepground.feed.feed.service;

import com.samsamhajo.deepground.feed.feed.entity.Feed;
import com.samsamhajo.deepground.feed.feed.entity.FeedMedia;
import com.samsamhajo.deepground.feed.feed.exception.FeedErrorCode;
import com.samsamhajo.deepground.feed.feed.exception.FeedException;
import com.samsamhajo.deepground.feed.feed.model.FeedCreateRequest;
import com.samsamhajo.deepground.feed.feed.model.FeedListResponse;
import com.samsamhajo.deepground.feed.feed.model.FeedResponse;
import com.samsamhajo.deepground.feed.feed.model.FeedUpdateRequest;
import com.samsamhajo.deepground.feed.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedMediaService feedMediaService;
    // TODO: private final MemberRepository memberRepository;

    @Transactional
    public Feed createFeed(FeedCreateRequest request, Long memberId) {
        if (!StringUtils.hasText(request.getContent())) {
            throw new FeedException(FeedErrorCode.INVALID_FEED_CONTENT);
        }

        //  TODO: Member member = memberRepository.getById(memberId);
        //        Feed feed = Feed.of(request.getContent(), member);

        Feed feed = Feed.of(request.getContent(), null);
        feedRepository.save(feed);

        saveFeedMedia(request, feed);

        return feed;
    }

    @Transactional
    public Feed updateFeed(Long feedId, FeedUpdateRequest request, Long memberId) {
        if (!StringUtils.hasText(request.getContent())) {
            throw new FeedException(FeedErrorCode.INVALID_FEED_CONTENT);
        }

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedErrorCode.FEED_NOT_FOUND));

        // TODO: 권한 체크 로직 추가 (본인 피드만 수정 가능)
        // if (!feed.getMember().getId().equals(memberId)) {
        //     throw new FeedException(FeedErrorCode.FEED_UPDATE_PERMISSION_DENIED);
        // }

        // 피드 내용 업데이트
        feed.updateContent(request.getContent());

        // 미디어 업데이트
        updateFeedMedia(feed, request);

        return feed;
    }


    public FeedListResponse getFeeds(Pageable pageable) {
        Page<Feed> feedPages = feedRepository.findAll(pageable);

        List<FeedResponse> feedResponse = feedPages.get().map(feed -> FeedResponse.of(
                feed.getId(),
                feed.getContent(),
                feed.getMember().getNickname(),
                feed.getCreatedAt(),
                feedMediaService.findAllByFeed(feed)
                        .stream()
                        .map(FeedMedia::getId)
                        .toList()
        )).toList();

        return FeedListResponse.of(
                feedResponse,
                feedPages.getNumber(),
                feedPages.getTotalPages()
        );
    }

    private void saveFeedMedia(FeedCreateRequest request, Feed feed) {
        feedMediaService.createFeedMedia(feed, request.getImages());
    }
    
    private void updateFeedMedia(Feed feed, FeedUpdateRequest request) {
        // 피드에 연결된 모든 미디어 삭제
        feedMediaService.deleteAllByFeedId(feed.getId());
        
        // 새 미디어 추가
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            feedMediaService.createFeedMedia(feed, request.getImages());
        }
    }
}