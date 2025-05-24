package com.samsamhajo.deepground.global.config;

import com.samsamhajo.deepground.auth.security.CustomUserDetails;
import com.samsamhajo.deepground.chat.service.ChatRoomMemberService;
import com.samsamhajo.deepground.utils.destination.DestinationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager.Builder;

@Configuration
@EnableWebSocketSecurity
@RequiredArgsConstructor
public class WebSocketSecurityConfig {

    private final ChatRoomMemberService chatRoomMemberService;

    @Bean
    public ChannelInterceptor csrfChannelInterceptor() {
        return new ChannelInterceptor() {
        };
    }

    @Bean
    AuthorizationManager<Message<?>> authorizationManager(Builder authorizationManager) {
        authorizationManager
                .nullDestMatcher().permitAll()
                .simpSubscribeDestMatchers("/user/**").authenticated()
                .simpSubscribeDestMatchers("/topic/chatrooms/**").access(chatRoomAuthorization())
                .simpDestMatchers("/app/chatrooms/**").access(chatRoomAuthorization())
                .anyMessage().denyAll();
        return authorizationManager.build();
    }

    private AuthorizationManager<MessageAuthorizationContext<?>> chatRoomAuthorization() {
        return (authorization, context) -> {
            Message<?> message = context.getMessage();
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            if (accessor == null || accessor.getUser() == null || accessor.getDestination() == null) {
                return new AuthorizationDecision(false);
            }

            String destination = accessor.getDestination();
            Long chatRoomId = DestinationUtils.extractChatRoomId(destination);

            if (chatRoomId == null) {
                return new AuthorizationDecision(false);
            }

            Authentication authentication = (Authentication) accessor.getUser();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Long memberId = userDetails.getMember().getId();
            boolean granted = chatRoomMemberService.isChatRoomMember(chatRoomId, memberId);
            return new AuthorizationDecision(granted);
        };
    }
}
