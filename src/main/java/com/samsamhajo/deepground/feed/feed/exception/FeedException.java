package com.samsamhajo.deepground.feed.feed.exception;

import com.samsamhajo.deepground.global.error.core.BaseException;

public class FeedException extends BaseException {
    public FeedException(FeedErrorCode errorCode) {
        super(errorCode);
    }
}