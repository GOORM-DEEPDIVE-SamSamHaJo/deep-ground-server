package com.samsamhajo.deepground.media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MediaExceptionTest {

    @Test
    @DisplayName("에러 코드로 MediaException을 생성할 수 있다")
    void constructor_withErrorCode() {
        // given
        MediaErrorCode errorCode = MediaErrorCode.MEDIA_NOT_FOUND;

        // when
        MediaException exception = new MediaException(errorCode);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    }

    @Test
    @DisplayName("에러 코드와 인자로 MediaException을 생성할 수 있다")
    void constructor_withErrorCodeAndArgs() {
        // given
        MediaErrorCode errorCode = MediaErrorCode.MEDIA_NOT_FOUND;
        Object[] args = {"테스트 파일", 123};

        // when
        MediaException exception = new MediaException(errorCode, args);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    }
} 