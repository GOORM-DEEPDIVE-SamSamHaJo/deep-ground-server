package com.samsamhajo.deepground.friend.controller;


import com.samsamhajo.deepground.friend.Dto.FriendDto;
import com.samsamhajo.deepground.friend.Dto.FriendRequestDto;
import com.samsamhajo.deepground.friend.Exception.FriendSuccessCode;
import com.samsamhajo.deepground.friend.service.FriendService;
import com.samsamhajo.deepground.global.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(("/friends"))
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<SuccessResponse> requestFriend(@RequestBody @Valid FriendRequestDto dto) {
        Long friendId = friendService.sendFriendRequest(dto.getRequesterId(), dto.getReceiverEmail());
        return ResponseEntity
                        .ok(SuccessResponse.of(FriendSuccessCode.FRIEND_SUCCESS_REQUEST,friendId));
    }


    @GetMapping("/sent")
    public ResponseEntity<List<FriendDto>> getSentFriendRequests(@RequestParam Long requesterId) {
        return ResponseEntity.ok(friendService.findSentFriendRequest(requesterId));
    }


    @PatchMapping("/sent/{friendId}/cancel")
    public ResponseEntity<SuccessResponse> cancelFriendRequest(@PathVariable Long friendId,
                                                           @RequestParam Long requesterId){
        friendService.cancelFriendRequest(friendId,requesterId);
        return ResponseEntity
                .ok(SuccessResponse.of(FriendSuccessCode.FRIEND_SUCCESS_CANCEL,friendId));

    }

    @GetMapping("/receive")
    public ResponseEntity<List<FriendDto>> getReceiveFriendRequests(@RequestParam Long receiverId) {
        return ResponseEntity.ok(friendService.findFriendReceive(receiverId));
    }

}
