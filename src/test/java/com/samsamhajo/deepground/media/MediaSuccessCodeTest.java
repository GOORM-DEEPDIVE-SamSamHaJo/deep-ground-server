package com.samsamhajo.deepground.media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class MediaSuccessCodeTest {

    @Test
    @DisplayName("MEDIA_FOUND는 200 상태 코드와 적절한 메시지를 가진다")
    void mediaFound_hasCorrectStatusAndMessage() {
        // given
        MediaSuccessCode successCode = MediaSuccessCode.MEDIA_FOUND;

        // when & then
        assertThat(successCode.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(successCode.getMessage()).isEqualTo("미디어 불러오기에 성공했습니다.");
    }

    @Test
    @DisplayName("모든 MediaSuccessCode는 유효한 상태 코드와 메시지를 가진다")
    void allSuccessCodes_haveValidStatusAndMessage() {
        // when & then
        for (MediaSuccessCode successCode : MediaSuccessCode.values()) {
            assertThat(successCode.getStatus()).isNotNull();
            assertThat(successCode.getMessage()).isNotBlank();
        }
    }
} 