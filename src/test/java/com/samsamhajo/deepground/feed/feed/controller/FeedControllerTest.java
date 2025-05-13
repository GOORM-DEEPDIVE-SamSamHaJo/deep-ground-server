package com.samsamhajo.deepground.feed.feed.controller;

import com.samsamhajo.deepground.feed.feed.entity.Feed;
import com.samsamhajo.deepground.feed.feed.exception.FeedSuccessCode;
import com.samsamhajo.deepground.feed.feed.model.FeedCreateRequest;
import com.samsamhajo.deepground.feed.feed.model.FeedListResponse;
import com.samsamhajo.deepground.feed.feed.model.FeedResponse;
import com.samsamhajo.deepground.feed.feed.service.FeedService;
import com.samsamhajo.deepground.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedService feedService;

    private Member member = Member.createLocalMember("choiyt3465@naver.com", "q1w2e3r4!", "yt");

    @Test
    @DisplayName("피드 생성 테스트")
    void createFeed() throws Exception {
        // given
        String content = "테스트 피드 내용";
        Long memberId = 1L;
        Feed feed = Feed.of(content, member);
        
        MockMultipartFile imagePart = new MockMultipartFile(
                "images", 
                "test-image.jpg", 
                "image/jpeg", 
                "이미지 테스트 데이터".getBytes()
        );
        
        given(feedService.createFeed(any(FeedCreateRequest.class), eq(memberId))).willReturn(feed);

        // when & then
        mockMvc.perform(multipart("/api/v1/feed")
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(FeedSuccessCode.FEED_CREATED.getStatus().value()))
                .andExpect(jsonPath("$.message").value(FeedSuccessCode.FEED_CREATED.getMessage()))
                .andExpect(jsonPath("$.result.content").value(content));
    }
    
    @Test
    @DisplayName("피드 목록 조회 테스트")
    void getFeeds() throws Exception {
        // given
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        FeedResponse feedResponse1 = FeedResponse.of(1L, "첫 번째 피드", "yt", LocalDateTime.now(), List.of(1L, 2L));
        FeedResponse feedResponse2 = FeedResponse.of(2L, "두 번째 피드", "yt", LocalDateTime.now(), List.of(3L));
        
        FeedListResponse feedListResponse = FeedListResponse.of(
                List.of(feedResponse1, feedResponse2),
                page,
                1
        );
        
        given(feedService.getFeeds(any(Pageable.class))).willReturn(feedListResponse);
        
        // when & then
        mockMvc.perform(get("/api/v1/feed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(FeedSuccessCode.FEEDS_RETRIEVED.getStatus().value()))
                .andExpect(jsonPath("$.message").value(FeedSuccessCode.FEEDS_RETRIEVED.getMessage()))
                .andExpect(jsonPath("$.result.feeds[0].id").value(1))
                .andExpect(jsonPath("$.result.feeds[0].content").value("첫 번째 피드"))
                .andExpect(jsonPath("$.result.feeds[0].memberName").value("yt"))
                .andExpect(jsonPath("$.result.feeds[0].imageIds[0]").value(1))
                .andExpect(jsonPath("$.result.feeds[0].imageIds[1]").value(2))
                .andExpect(jsonPath("$.result.feeds[1].id").value(2))
                .andExpect(jsonPath("$.result.feeds[1].content").value("두 번째 피드"))
                .andExpect(jsonPath("$.result.currentPage").value(page))
                .andExpect(jsonPath("$.result.totalPages").value(1));
    }
} 