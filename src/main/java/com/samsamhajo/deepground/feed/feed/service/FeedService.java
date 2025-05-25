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
import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.exception.MemberErrorCode;
import com.samsamhajo.deepground.member.exception.MemberException;
import com.samsamhajo.deepground.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    @Transactional
    public Feed createFeed(FeedCreateRequest request, Long memberId) {
        if (!StringUtils.hasText(request.getContent())) {
            throw new FeedException(FeedErrorCode.INVALID_FEED_CONTENT);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));

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

        // TODO: 권한 체크 로직 추가 (본인 피드만 수정 가능)
        // if (!feed.getMember().getId().equals(memberId)) {
        //     throw new FeedException(FeedErrorCode.FEED_UPDATE_PERMISSION_DENIED);
        // }

        // 피드 내용 업데이트
        feed.updateContent(request.getContent());

        // 미디어 업데이트
        feedMediaService.updateFeedMedia(feed, request);

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

    @Transactional
    public void deleteFeed(Long feedId, Long memberId) {
        Feed feed = feedRepository.getById(feedId);
        
        // 권한 체크 - 본인 피드만 삭제 가능
        if (!feed.getMember().getId().equals(memberId)) {
            throw new FeedException(FeedErrorCode.FEED_UPDATE_PERMISSION_DENIED);
        }
        
        feed.softDelete();
        // TODO: FeedLike SoftDelete
        
        feedMediaService.deleteAllByFeedId(feedId);
    }
}