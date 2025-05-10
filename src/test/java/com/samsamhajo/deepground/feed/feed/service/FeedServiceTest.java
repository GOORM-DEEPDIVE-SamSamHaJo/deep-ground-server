package com.samsamhajo.deepground.feed.feed.service;

import com.samsamhajo.deepground.feed.feed.entity.Feed;
import com.samsamhajo.deepground.feed.feed.exception.FeedErrorCode;
import com.samsamhajo.deepground.feed.feed.exception.FeedException;
import com.samsamhajo.deepground.feed.feed.model.FeedCreateRequest;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @InjectMocks
    private FeedService feedService;

    @Mock
    private FeedRepository feedRepository;

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
} 