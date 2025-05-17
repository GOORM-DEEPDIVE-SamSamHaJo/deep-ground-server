package com.samsamhajo.deepground.friend.service;

import com.samsamhajo.deepground.friend.Dto.FriendDto;
import com.samsamhajo.deepground.friend.Exception.FriendException;
import com.samsamhajo.deepground.friend.entity.Friend;
import com.samsamhajo.deepground.friend.Exception.FriendErrorCode;
import com.samsamhajo.deepground.friend.entity.FriendStatus;
import com.samsamhajo.deepground.friend.repository.FriendRepository;
import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long sendFriendRequest (Long requesterId, String receiverEmail) {

        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.INVALID_MEMBER_EMAIL));

        Member receiver = memberRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new FriendException(FriendErrorCode.INVALID_MEMBER_EMAIL));

        validateSendRequest(requester, receiver, receiverEmail);

        Friend friend = Friend.request(requester, receiver);
        friendRepository.save(friend);

        return friend.getId();
    }
    private void validateSendRequest(Member requester, Member receiver,String receiverEmail) {

        if(receiverEmail == null || receiverEmail.trim().isEmpty()){
            throw new FriendException(FriendErrorCode.BLANK_EMAIL);
        }
        if(requester.getEmail().equals(receiver.getEmail())) {
            throw new FriendException(FriendErrorCode.SELF_REQUEST);
        }
        if(friendRepository.existsByRequestMemberAndReceiveMemberAndStatus(requester, receiver, FriendStatus.ACCEPT)) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIEND);
        }
        if(friendRepository.existsByRequestMemberAndReceiveMemberAndStatus(requester,receiver, FriendStatus.REQUEST)) {
            throw new FriendException(FriendErrorCode.ALREADY_REQUESTED);
        }
    }
    public List<FriendDto> findSentFriendRequest(Long requesterId) {
        List<Friend> friends = friendRepository.findSentRequests(requesterId);
        return friends.stream()
                .map(FriendDto::fromSent)
                .toList();
    }

    @Transactional
    public Long cancelFriendRequest(Long friendId, Long requesterId) {
        Friend friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendException(FriendErrorCode. INVALID_FRIEND_REQUEST));

        friendRequest.cancel(requesterId);

        return friendRequest.getId();

    }

    public List<FriendDto> findFriendReceive (Long receiverId) {
        List<Friend> friends = friendRepository.findReceiveRequests(receiverId);
        return friends.stream()
                .map(FriendDto::fromReceived)
                .toList();
    }
}