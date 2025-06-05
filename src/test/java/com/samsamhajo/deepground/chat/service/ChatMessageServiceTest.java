package com.samsamhajo.deepground.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.samsamhajo.deepground.chat.dto.ChatMessageRequest;
import com.samsamhajo.deepground.chat.dto.ChatMessageResponse;
import com.samsamhajo.deepground.chat.entity.ChatMedia;
import com.samsamhajo.deepground.chat.entity.ChatMessage;
import com.samsamhajo.deepground.chat.entity.ChatMessageMedia;
import com.samsamhajo.deepground.chat.exception.ChatMessageErrorCode;
import com.samsamhajo.deepground.chat.exception.ChatMessageException;
import com.samsamhajo.deepground.chat.repository.ChatMediaRepository;
import com.samsamhajo.deepground.chat.repository.ChatMessageRepository;
import com.samsamhajo.deepground.global.message.MessagePublisher;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceTest {

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatMediaRepository chatMediaRepository;

    private final Long chatRoomId = 1L;
    private final Long memberId = 1L;

    @Test
    @DisplayName("채팅 메시지를 전송한다")
    void sendMessage_success() {
        // given
        String message = "테스트 채팅 메시지";
        ChatMessageRequest request = ChatMessageRequest.builder()
                .message(message)
                .build();
        ChatMessage chatMessage = ChatMessage.of(chatRoomId, memberId, message);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        // when
        chatMessageService.sendMessage(chatRoomId, memberId, request);

        // then
        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());

        ChatMessage savedChatMessage = captor.getValue();
        assertThat(savedChatMessage.getChatRoomId()).isEqualTo(chatRoomId);
        assertThat(savedChatMessage.getSenderId()).isEqualTo(memberId);
        assertThat(savedChatMessage.getMessage()).isEqualTo(message);

        String expectedDestination = "/chatrooms/" + chatRoomId + "/message";
        verify(messagePublisher).convertAndSend(eq(expectedDestination), any(ChatMessageResponse.class));
    }

    @Test
    @DisplayName("메시지와 미디어가 모두 비어있으면 예외가 발생한다")
    void sendMessage_withEmptyContent_throwsException() {
        // given
        ChatMessageRequest request = ChatMessageRequest.builder()
                .build();

        // when & then
        assertThatThrownBy(() -> chatMessageService.sendMessage(chatRoomId, memberId, request))
                .isInstanceOf(ChatMessageException.class)
                .hasMessage(ChatMessageErrorCode.INVALID_MESSAGE.getMessage());
    }

    @Test
    @DisplayName("미디어와 채팅 메시지를 전송한다")
    void sendMessage_withMedia_success() {
        // given
        List<String> mediaIds = List.of("1234");
        ChatMessageMedia messageMedia = ChatMessageMedia.of("/media/test.jpg", "test.jpg", 0L, "jpg");
        List<ChatMedia> chatMedia = List.of(ChatMedia.of(chatRoomId, memberId, messageMedia));

        String message = "테스트 채팅 메시지";
        ChatMessageRequest request = ChatMessageRequest.builder()
                .message(message)
                .mediaIds(mediaIds)
                .build();
        ChatMessage chatMessage = ChatMessage.of(chatRoomId, memberId, message);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);
        when(chatMediaRepository.findAllByIdAndStatusPending(mediaIds)).thenReturn(chatMedia);

        // when
        chatMessageService.sendMessage(chatRoomId, memberId, request);

        // then
        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());

        ChatMessage savedChatMessage = captor.getValue();
        assertThat(savedChatMessage.getChatRoomId()).isEqualTo(chatRoomId);
        assertThat(savedChatMessage.getSenderId()).isEqualTo(memberId);
        assertThat(savedChatMessage.getMessage()).isEqualTo(message);
        assertThat(savedChatMessage.getMedia()).isEqualTo(List.of(messageMedia));

        String expectedDestination = "/chatrooms/" + chatRoomId + "/message";
        verify(messagePublisher).convertAndSend(eq(expectedDestination), any(ChatMessageResponse.class));
    }
}
