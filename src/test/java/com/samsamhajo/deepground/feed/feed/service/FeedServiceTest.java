package com.samsamhajo.deepground.feed.feed.service;

import com.samsamhajo.deepground.feed.feed.entity.Feed;
import com.samsamhajo.deepground.feed.feed.entity.FeedMedia;
import com.samsamhajo.deepground.feed.feed.exception.FeedErrorCode;
import com.samsamhajo.deepground.feed.feed.exception.FeedException;
import com.samsamhajo.deepground.feed.feed.model.FeedCreateRequest;
import com.samsamhajo.deepground.feed.feed.model.FeedListResponse;
import com.samsamhajo.deepground.feed.feed.model.FeedResponse;
import com.samsamhajo.deepground.feed.feed.repository.FeedRepository;
import com.samsamhajo.deepground.member.entity.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @InjectMocks
    private FeedService feedService;

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private FeedMediaService feedMediaService;

//    TODO: @Mock
//          private MemberRepository memberRepository;

    private Member member = Member.createLocalMember("choiyt3465@naver.com", "q1w2e3r4!", "yt");


    @Mock
    private PlatformTransactionManager transactionManager;

    private TransactionStatus status;

    @BeforeEach
    void setUp() {
        // 테스트 시작 전 트랜잭션 시작
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @AfterEach
    void tearDown() {
        // 테스트 종료 후 롤백 처리
        if (status != null) {
            transactionManager.rollback(status);
        }
    }

    @Test
    @DisplayName("피드 생성 성공 테스트")
    void createFeed_Success() {
        // given
        String content = "테스트 피드 내용";
        Feed feed = Feed.of(content, member);
        Long memberId = 1L;

        //   given(memberRepository.getById(memberId)).willReturn(member);
        given(feedRepository.save(any(Feed.class))).willReturn(feed);

        // when
        Feed createdFeed = feedService.createFeed(new FeedCreateRequest(content,null), memberId);

        // then
        assertThat(createdFeed).isNotNull();
        assertThat(createdFeed.getContent()).isEqualTo(content);


        // TODO: memberId를 사용하여 Member 객체를 가져오는 로직이 필요합니다.
        //  assertThat(createdFeed.getMember().getId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("피드 생성 실패 테스트 - 빈 내용")
    void createFeed_Fail_EmptyContent() {
        // given
        String content = "";
        Long memberId = 1L;

        // when & then
        assertThatThrownBy(() -> feedService.createFeed(new FeedCreateRequest(content,null), memberId))
                .isInstanceOf(FeedException.class)
                .hasFieldOrPropertyWithValue("errorCode", FeedErrorCode.INVALID_FEED_CONTENT);
    }
    
    @Test
    @DisplayName("피드 목록 조회 테스트")
    void getFeeds_Success() {
        // given
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Feed feed1 = Feed.of("첫 번째 피드", member);
        Feed feed2 = Feed.of("두 번째 피드", member);
        
        List<Feed> feeds = List.of(feed1, feed2);
        Page<Feed> feedPage = new PageImpl<>(feeds, pageable, feeds.size());
        
        List<FeedMedia> feed1Medias = List.of(
                FeedMedia.of("url1", "jpg", feed1),
                FeedMedia.of("url2", "jpg", feed1)
        );
        
        List<FeedMedia> feed2Medias = List.of(
                FeedMedia.of("url3", "jpg", feed2)
        );
        
        // Mock 설정
        when(feedRepository.findAll(pageable)).thenReturn(feedPage);
        when(feedMediaService.findAllByFeed(feed1)).thenReturn(feed1Medias);
        when(feedMediaService.findAllByFeed(feed2)).thenReturn(feed2Medias);
        
        // when
        FeedListResponse result = feedService.getFeeds(pageable);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getFeeds()).hasSize(2);
        assertThat(result.getCurrentPage()).isEqualTo(page);
        assertThat(result.getTotalPages()).isEqualTo(1);
        
        // 첫 번째 피드 검증
        FeedResponse firstFeed = result.getFeeds().get(0);
        assertThat(firstFeed.getContent()).isEqualTo("첫 번째 피드");
        assertThat(firstFeed.getMemberName()).isEqualTo("yt");
        assertThat(firstFeed.getImageIds()).hasSize(2);
        
        // 두 번째 피드 검증
        FeedResponse secondFeed = result.getFeeds().get(1);
        assertThat(secondFeed.getContent()).isEqualTo("두 번째 피드");
        assertThat(secondFeed.getMemberName()).isEqualTo("yt");
        assertThat(secondFeed.getImageIds()).hasSize(1);
    }
}