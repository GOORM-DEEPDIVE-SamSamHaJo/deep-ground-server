package com.samsamhajo.deepground.feed.feed.controller;

import com.samsamhajo.deepground.feed.feed.entity.Feed;
import com.samsamhajo.deepground.feed.feed.exception.FeedSuccessCode;
import com.samsamhajo.deepground.feed.feed.model.FeedCreateRequest;
import com.samsamhajo.deepground.feed.feed.service.FeedService;
import com.samsamhajo.deepground.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
                        .param("memberId", memberId.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(jsonPath("$.status").value(FeedSuccessCode.FEED_CREATED.getStatus().value()))
                .andExpect(jsonPath("$.message").value(FeedSuccessCode.FEED_CREATED.getMessage()))
                .andExpect(jsonPath("$.result.content").value(content));
    }
} 