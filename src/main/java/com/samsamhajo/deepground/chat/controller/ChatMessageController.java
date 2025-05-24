package com.samsamhajo.deepground.chat.controller;

import com.samsamhajo.deepground.auth.security.CustomUserDetails;
import com.samsamhajo.deepground.chat.dto.ChatMessageRequest;
import com.samsamhajo.deepground.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/chatrooms/{chatRoomId}")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/message")
    public void message(
            @DestinationVariable Long chatRoomId,
            @Payload ChatMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMember().getId();
        chatMessageService.sendMessage(chatRoomId, memberId, request);
    }
}
