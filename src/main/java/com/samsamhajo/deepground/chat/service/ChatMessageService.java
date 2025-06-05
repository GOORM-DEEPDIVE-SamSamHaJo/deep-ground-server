package com.samsamhajo.deepground.chat.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final MessagePublisher messagePublisher;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMediaRepository chatMediaRepository;

    @Transactional
    public void sendMessage(Long chatRoomId, Long memberId, ChatMessageRequest request) {
        boolean hasMedia = !CollectionUtils.isEmpty(request.getMediaIds());

        if (!StringUtils.hasText(request.getMessage()) && !hasMedia) {
            throw new ChatMessageException(ChatMessageErrorCode.INVALID_MESSAGE);
        }

        ChatMessage chatMessage = hasMedia ? messageTextAndMedia(chatRoomId, memberId, request)
                : ChatMessage.of(chatRoomId, memberId, request.getMessage());
        chatMessageRepository.save(chatMessage);

        // 채팅 메시지 전송
        String destination = "/chatrooms/" + chatRoomId + "/message";
        messagePublisher.convertAndSend(destination, ChatMessageResponse.from(chatMessage));

        // TODO: 읽지 않은 메시지 개수 로직
    }

    private ChatMessage messageTextAndMedia(Long chatRoomId, Long memberId, ChatMessageRequest request) {
        List<ChatMedia> chatMedia = chatMediaRepository.findAllByIdAndStatusPending(request.getMediaIds());
        List<ChatMessageMedia> media = chatMedia.stream()
                .filter(m -> m.getChatRoomId().equals(chatRoomId) && m.getMemberId().equals(memberId))
                .map(ChatMedia::getMedia)
                .toList();

        if (request.getMediaIds().size() != media.size()) {
            throw new ChatMessageException(ChatMessageErrorCode.MEDIA_NOT_FOUND);
        }

        chatMedia.forEach(ChatMedia::send);
        chatMediaRepository.saveAll(chatMedia);

        return ChatMessage.of(chatRoomId, memberId, request.getMessage(), media);
    }
}
