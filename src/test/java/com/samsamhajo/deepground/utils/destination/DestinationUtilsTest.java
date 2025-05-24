package com.samsamhajo.deepground.utils.destination;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DestinationUtilsTest {

    @ParameterizedTest
    @CsvSource({
            "/topic/chatrooms/1/message, 1",
            "/app/chatrooms/1234/message, 1234",
    })
    @DisplayName("유효한 채팅방 목적지에서 chatRoomId를 추출한다")
    void extractChatRoomId_validDestinations(String destination, Long expectedChatRoomId) {
        // when
        Long chatRoomId = DestinationUtils.extractChatRoomId(destination);

        // then
        assertThat(chatRoomId).isNotNull();
        assertThat(chatRoomId).isEqualTo(expectedChatRoomId);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/topic/chatrooms/",
            "/topic/chatrooms/abc/message",
            "/app/chatrooms/abc/message"
    })
    @DisplayName("유효하지 않은 채팅방 목적지에서 null을 반환한다")
    void extractChatRoomId_invalidDestinations(String destination) {
        // when
        Long chatRoomId = DestinationUtils.extractChatRoomId(destination);

        // then
        assertThat(chatRoomId).isNull();
    }
}
