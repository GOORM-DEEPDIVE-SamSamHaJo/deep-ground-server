package com.samsamhajo.deepground.chat.dto;

import com.samsamhajo.deepground.chat.entity.ChatRoomMember;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomMemberInfo {

    private Long memberId;
    private String nickname;
    private LocalDateTime lastReadMessageTime;

    public static ChatRoomMemberInfo from(ChatRoomMember member) {
        return ChatRoomMemberInfo.builder()
                .memberId(member.getMember().getId())
                .nickname(member.getMember().getNickname())
                .lastReadMessageTime(member.getLastReadMessageTime())
                .build();
    }
}
