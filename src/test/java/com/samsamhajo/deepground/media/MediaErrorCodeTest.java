package com.samsamhajo.deepground.media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class MediaErrorCodeTest {

    @Test
    @DisplayName("MEDIA_NOT_FOUND는 404 상태 코드와 적절한 메시지를 가진다")
    void mediaNotFound_hasCorrectStatusAndMessage() {
        // given
        MediaErrorCode errorCode = MediaErrorCode.MEDIA_NOT_FOUND;

        // when & then
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getMessage()).isEqualTo("미디어를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("모든 MediaErrorCode는 유효한 상태 코드와 메시지를 가진다")
    void allErrorCodes_haveValidStatusAndMessage() {
        // when & then
        for (MediaErrorCode errorCode : MediaErrorCode.values()) {
            assertThat(errorCode.getStatus()).isNotNull();
            assertThat(errorCode.getMessage()).isNotBlank();
        }
    }
} 