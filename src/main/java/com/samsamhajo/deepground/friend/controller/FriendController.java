package com.samsamhajo.deepground.friend.controller;


import com.samsamhajo.deepground.friend.Dto.FriendDto;
import com.samsamhajo.deepground.friend.Dto.FriendRequestDto;
import com.samsamhajo.deepground.friend.Exception.FriendSuccessCode;
import com.samsamhajo.deepground.friend.service.FriendService;
import com.samsamhajo.deepground.global.success.SuccessResponse;
import com.samsamhajo.deepground.member.entity.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(("/friends"))
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<SuccessResponse> requestFriend(@RequestBody @Valid FriendRequestDto dto,
                                                         @AuthenticationPrincipal Member member) {
        Long friendId = friendService.sendFriendRequest(member.getId(), dto.getReceiverEmail());
        return ResponseEntity
                        .ok(SuccessResponse.of(FriendSuccessCode.FRIEND_SUCCESS_REQUEST,friendId));
    }


    @GetMapping("/sent")
    public ResponseEntity<SuccessResponse<List<FriendDto>>> getSentFriendRequests(@AuthenticationPrincipal Member member) {
        List<FriendDto> sentList = friendService.findSentFriendRequest(member.getId());
        return ResponseEntity.ok(
                SuccessResponse.of(FriendSuccessCode.FRIEND_SENT_LIST_FOUND, sentList));
    }


    @PatchMapping("/sent/{friendId}/cancel")
    public ResponseEntity<SuccessResponse> cancelFriendRequest(@PathVariable Long friendId,
                                                               @AuthenticationPrincipal Member member){
        friendService.cancelFriendRequest(friendId, member.getId());
        return ResponseEntity
                .ok(SuccessResponse.of(FriendSuccessCode.FRIEND_SUCCESS_CANCEL,friendId));

    }

    @GetMapping("/receive")
    public ResponseEntity<SuccessResponse<List<FriendDto>>> getReceiveFriendRequests(@AuthenticationPrincipal Member member) {
        List<FriendDto> receiveList = friendService.findFriendReceive(member.getId());
        return ResponseEntity
                .ok(SuccessResponse.of(FriendSuccessCode.FRIEND_RECEIVE_LIST_FOUND, receiveList));
    }

    @PatchMapping("/receive/{friendId}/accept")
    public ResponseEntity<SuccessResponse> acceptFriendRequest(@PathVariable Long friendId,
                                                               @AuthenticationPrincipal Member member){
        Long result = friendService.acceptFriendRequest(friendId, member.getId());
        return ResponseEntity
                .ok(SuccessResponse.of(FriendSuccessCode.FRIEND_SUCCESS_ACCEPT,result));
    }

    @PatchMapping("/receive/{friendId}/refusal")
    public ResponseEntity<SuccessResponse> refusalFriendRequest(@PathVariable Long friendId,
                                                                @AuthenticationPrincipal Member member){
        friendService.refusalFriendRequest(friendId,member.getId());
        return ResponseEntity
                .ok(SuccessResponse.of(FriendSuccessCode.FRIEND_SUCCESS_REFUSAL,friendId));

    }

    @PostMapping("/from-profile/{receiverId}")
    public ResponseEntity<SuccessResponse> requestProfileFriend(@PathVariable Long receiverId,
                                                                @AuthenticationPrincipal Member member) {
        Long friendId = friendService.sendProfileFriendRequest(member.getId(), receiverId);

        return ResponseEntity
                .ok(SuccessResponse.of(FriendSuccessCode.FRIEND_SUCCESS_REQUEST,friendId));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<SuccessResponse> deleteFriend(@PathVariable Long friendId) {

        friendService.deleteFriendById(friendId);
        return ResponseEntity
                .ok(SuccessResponse.of(FriendSuccessCode.FRIEND_SUCCESS_DELETE, friendId));
    }

    @GetMapping
    public  ResponseEntity<SuccessResponse<List<FriendDto>>> getFriendList(@AuthenticationPrincipal Member member){
        List<FriendDto> friends = friendService.getFriendByMemberId(member.getId());
        return ResponseEntity
                .ok(SuccessResponse.of(FriendSuccessCode.FRIEND_SUCCESS_GET_LIST,friends));
    }
}
