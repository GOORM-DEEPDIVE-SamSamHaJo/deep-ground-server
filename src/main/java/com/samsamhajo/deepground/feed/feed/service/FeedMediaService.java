package com.samsamhajo.deepground.feed.feed.service;

import com.samsamhajo.deepground.feed.feed.entity.Feed;
import com.samsamhajo.deepground.feed.feed.entity.FeedMedia;
import com.samsamhajo.deepground.feed.feed.model.FeedMediaResponse;
import com.samsamhajo.deepground.feed.feed.repository.FeedMediaRepository;
import com.samsamhajo.deepground.media.MediaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedMediaService {

    private final FeedMediaRepository feedMediaRepository;

    public void createFeedMedia(Feed feed, List<MultipartFile> images){
        feedMediaRepository.saveAll(
                images.stream()
                        .map(image -> FeedMedia.of(
                                MediaUtils.generateMediaUrl(image),
                                MediaUtils.getExtension(image),
                                feed))
                        .toList()
        );
    }

    public FeedMediaResponse fetchFeedMedia(Long feedMediaId) {
        FeedMedia feedMedia = feedMediaRepository.getById(feedMediaId);
        InputStreamResource image = MediaUtils.getMedia(feedMedia.getMediaUrl());

        return FeedMediaResponse.of(image, feedMedia.getExtension());
    }

    public List<FeedMedia> findAllByFeed(Feed feed) {
        return feedMediaRepository.findAllByFeedId(feed.getId());
    }
}