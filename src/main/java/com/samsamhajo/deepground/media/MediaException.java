package com.samsamhajo.deepground.media;

import com.samsamhajo.deepground.global.error.core.BaseException;
import com.samsamhajo.deepground.global.error.core.ErrorCode;

public class MediaException extends BaseException {

    public MediaException(MediaErrorCode errorCode) {
        super(errorCode);
    }

    public MediaException(MediaErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
