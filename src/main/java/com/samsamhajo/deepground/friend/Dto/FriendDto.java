package com.samsamhajo.deepground.friend.Dto;

import com.samsamhajo.deepground.friend.entity.Friend;
import com.samsamhajo.deepground.friend.entity.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class FriendDto {

    private Long friendId;
    private String otherMemberName;
    private FriendStatus status;

    private FriendDto(Long friendId, String otherMemberName, FriendStatus status) {
        this.friendId = friendId;
        this.otherMemberName = otherMemberName;
        this.status = status;
    }

    public static FriendDto fromSent(Friend friend) {
        return new FriendDto(
                friend.getId(),
                friend.getReceiveMember().getNickname(),
                friend.getStatus()
        );
    }

    public static FriendDto fromReceived(Friend friend) {
        return new FriendDto(
                friend.getId(),
                friend.getRequestMember().getNickname(),
                friend.getStatus()
        );
    }

}