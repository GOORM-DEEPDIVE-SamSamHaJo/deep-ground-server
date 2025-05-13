package com.samsamhajo.deepground.feed.feed.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class FeedUpdateRequest {
    private String content;
    private List<MultipartFile> images;
}