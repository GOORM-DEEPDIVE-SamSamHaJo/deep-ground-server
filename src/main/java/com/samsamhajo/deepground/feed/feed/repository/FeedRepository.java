package com.samsamhajo.deepground.feed.feed.repository;

import com.samsamhajo.deepground.feed.feed.entity.Feed;
import com.samsamhajo.deepground.feed.feed.exception.FeedErrorCode;
import com.samsamhajo.deepground.feed.feed.exception.FeedException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {
    default Feed getById(Long id) {
        return findById(id).orElseThrow(() -> new FeedException(FeedErrorCode.FEED_NOT_FOUND));
    }
}
