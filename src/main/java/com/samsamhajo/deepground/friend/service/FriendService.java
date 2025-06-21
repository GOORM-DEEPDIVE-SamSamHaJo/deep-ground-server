package com.samsamhajo.deepground.friend.service;

import com.samsamhajo.deepground.friend.Dto.FriendDto;
import com.samsamhajo.deepground.friend.Exception.FriendException;
import com.samsamhajo.deepground.friend.entity.Friend;
import com.samsamhajo.deepground.friend.Exception.FriendErrorCode;
import com.samsamhajo.deepground.friend.entity.FriendStatus;
import com.samsamhajo.deepground.friend.repository.FriendRepository;
import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.exception.MemberErrorCode;
import com.samsamhajo.deepground.member.exception.MemberException;
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
    public Long sendFriendRequest(Long requesterId, String receiverEmail) {

        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL));

        Member receiver = memberRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL));

        validateSendRequest(requester, receiver, receiverEmail);

        Friend friend = Friend.request(requester, receiver);
        friendRepository.save(friend);

        return friend.getId();
    }

    private void validateSendRequest(Member requester, Member receiver, String receiverEmail) {

        if (receiverEmail == null || receiverEmail.trim().isEmpty()) {
            throw new FriendException(FriendErrorCode.BLANK_EMAIL);
        }
        if (requester.getEmail().equals(receiver.getEmail())) {
            throw new FriendException(FriendErrorCode.SELF_REQUEST);
        }
        if (friendRepository.existsByRequestMemberAndReceiveMemberAndStatus(requester, receiver, FriendStatus.ACCEPT)) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIEND);
        }
        if (friendRepository.existsByRequestMemberAndReceiveMemberAndStatus(requester, receiver, FriendStatus.REQUEST)) {
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
                .orElseThrow(() -> new FriendException(FriendErrorCode.INVALID_FRIEND_REQUEST));

        friendRequest.cancel(requesterId);

        return friendRequest.getId();

    }

    public List<FriendDto> findFriendReceive(Long receiverId) {
        List<Friend> friends = friendRepository.findReceiveRequests(receiverId);
        return friends.stream()
                .map(FriendDto::fromReceived)
                .toList();
    }

    @Transactional
    public Long acceptFriendRequest(Long friendId, Long receiverId) {
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));

        Friend friendRequest = validateAccept(friendId, receiver);

        friendRequest.accept();

        return friendRequest.getId();
    }

    private Friend validateAccept(Long friendId, Member receiver) {
        Friend friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.INVALID_FRIEND_REQUEST));

        if (!friendRequest.getReceiveMember().equals(receiver)) {
            throw new FriendException(FriendErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (friendRepository.existsByIdAndReceiveMemberAndStatus(friendId, receiver, FriendStatus.ACCEPT)) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIEND);
        }
        return friendRequest;
    }

    @Transactional
    public Long refusalFriendRequest(Long friendId, Long receiverId) {
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));

        Friend friendRequest = validateRefusal(friendId, receiver);

        friendRequest.refusal();

        return friendRequest.getId();
    }

    private Friend validateRefusal(Long friendId, Member receiver) {

        Friend friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.INVALID_FRIEND_REQUEST));

        if (!friendRequest.getReceiveMember().equals(receiver)) {
            throw new FriendException(FriendErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (friendRepository.existsByIdAndReceiveMemberAndStatus(friendId, receiver, FriendStatus.ACCEPT)) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIEND);
        }

        return friendRequest;
    }

    @Transactional
    public Long sendProfileFriendRequest(Long memberId, Long receiverId) {

        Member requester = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));

        validateSendProfileRequest(requester, receiver);

        Friend friend = Friend.request(requester, receiver);
        friendRepository.save(friend);

        return friend.getId();
    }

    private void validateSendProfileRequest(Member requester, Member receiver) {

        if (requester.getId().equals(receiver.getId())) {
            throw new FriendException(FriendErrorCode.SELF_REQUEST);
        }
        if (friendRepository.existsByRequestMemberAndReceiveMemberAndStatus(requester, receiver, FriendStatus.ACCEPT)) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIEND);
        }
        if (friendRepository.existsByRequestMemberAndReceiveMemberAndStatus(requester, receiver, FriendStatus.REQUEST)) {
            throw new FriendException(FriendErrorCode.ALREADY_REQUESTED);
        }
        if (friendRepository.existsByRequestMemberAndReceiveMemberAndStatus(receiver, requester, FriendStatus.REQUEST)) {
            throw new FriendException(FriendErrorCode.REQUEST_ALREADY_RECEIVED);
        }
    }


    @Transactional
    public void deleteFriendByMemberId(Long friendId, Long memberId) {

        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.INVALID_FRIEND));

        boolean isRequester = friend.getRequestMember().getId().equals(memberId);
        boolean isReceiver = friend.getReceiveMember().getId().equals(memberId);

        if (!isRequester && !isReceiver) {
            throw new FriendException(FriendErrorCode.UNAUTHORIZED_ACCESS);
        }
        friend.softDelete();


    }

    public List<FriendDto> getFriendByMemberId(Long memberId) {

            List<Friend> friends = friendRepository. findAllByMemberIdAndFriendStatusAndDeletedIs(memberId, FriendStatus.ACCEPT);

        return friends.stream()
                .map(friend -> {
                    if (friend.getReceiveMember().getId().equals(memberId)) {
                        return FriendDto.fromReceived(friend);
                    }
                    return FriendDto.fromSent(friend);
                })
                .toList();
    }


}