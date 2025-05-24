package com.samsamhajo.deepground.auth.jwt;

import com.samsamhajo.deepground.auth.security.CustomUserDetailsService;
import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private final MemberRepository memberRepository;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            connect(accessor);
        }
        return message;
    }

    private void connect(StompHeaderAccessor accessor) {
        String jwt = resolveToken(accessor);

        if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
            Long memberId = jwtProvider.getMemberId(jwt);
            Member member = memberRepository.findById(memberId).orElse(null);
            if (member != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                accessor.setUser(authentication);
            }
        }
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
