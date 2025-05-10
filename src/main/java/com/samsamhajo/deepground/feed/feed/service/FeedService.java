package com.samsamhajo.deepground.feed.feed.service;

import com.samsamhajo.deepground.feed.feed.entity.Feed;
import com.samsamhajo.deepground.feed.feed.exception.FeedErrorCode;
import com.samsamhajo.deepground.feed.feed.exception.FeedException;
import com.samsamhajo.deepground.feed.feed.model.FeedCreateRequest;
import com.samsamhajo.deepground.feed.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    // TODO: private final MemberRepository memberRepository;

    @Transactional
    public Feed createFeed(FeedCreateRequest request, Long memberId) {
        if (!StringUtils.hasText(request.getContent())) {
            throw new FeedException(FeedErrorCode.INVALID_FEED_CONTENT);
        }
        //  TODO : 이미지 추가 로직 작성

        //  TODO: Member member = memberRepository.getById(memberId);
        //        Feed feed = Feed.of(content, member);

        Feed feed = Feed.of(request.getContent(), null);
        return feedRepository.save(feed);
    }
}