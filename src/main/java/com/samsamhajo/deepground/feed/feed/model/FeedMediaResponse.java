package com.samsamhajo.deepground.feed.feed.model;

import lombok.Getter;
import org.springframework.core.io.InputStreamResource;

@Getter
public class FeedMediaResponse {
    private final InputStreamResource image;
    private final String extension;

    private FeedMediaResponse(InputStreamResource image, String extension) {
        this.image = image;
        this.extension = extension;
    }

    public static FeedMediaResponse of(InputStreamResource image, String extension) {
        return new FeedMediaResponse(image, extension);
    }
}