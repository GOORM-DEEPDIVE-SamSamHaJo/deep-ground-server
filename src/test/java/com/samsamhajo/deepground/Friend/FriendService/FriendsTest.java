package com.samsamhajo.deepground.Friend.FriendService;



import com.samsamhajo.deepground.friend.Dto.FriendDto;
import com.samsamhajo.deepground.friend.entity.Friend;
import com.samsamhajo.deepground.friend.repository.FriendRepository;
import com.samsamhajo.deepground.friend.service.FriendService;
import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
public class FriendsTest {

    @Autowired
    private FriendService friendService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRepository friendRepository;


    private Member requester;
    private Member requester2;
    private Member requester3;
    private Member receiver;
    private Member receiver2;

    @BeforeEach
    void setup() {
        requester = Member.createLocalMember("paka@gamil.com", "pw", "파카");
        requester2 = Member.createLocalMember("gar@gmail.com", "pw", "가");
        requester3 = Member.createLocalMember("den@gmail.com", "pw", "든");
        receiver = Member.createLocalMember("garden@gmail.com", "pw", "가든");
        receiver2 = Member.createLocalMember("pa@gmail.com","pw","파카");


        memberRepository.save(requester);
        memberRepository.save(requester2);
        memberRepository.save(requester3);
        memberRepository.save(receiver);
        memberRepository.save(receiver2);

    }

    @Test
    public void 친구_삭제_성공() throws Exception {
        //given
        Long friendId = friendService.sendFriendRequest(requester.getId(), receiver.getEmail());
        friendService.acceptFriendRequest(friendId, receiver.getId());

        //when
        friendService.deleteFriendById(friendId);

        Friend deletedFriend = friendRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("삭제된 친구가 DB에 존재하지 않음"));

        assertTrue(deletedFriend.isDeleted());
    }

    @Test
    public void 친구_목록() throws Exception {
        //given
        Long friendId = friendService.sendFriendRequest(requester.getId(), receiver.getEmail());
        Long friendId2 = friendService.sendFriendRequest(requester.getId(), receiver2.getEmail());

        friendService.acceptFriendRequest(friendId, receiver.getId());
        friendService.acceptFriendRequest(friendId2, receiver2.getId());
        //when
        List<FriendDto> friends = friendService.getFriendByMemberId(requester.getId());

        //then

        assertTrue(friends.stream().anyMatch(f -> f.getOtherMemberName().equals(receiver.getNickname())));
        assertTrue(friends.stream().anyMatch(f -> f.getOtherMemberName().equals(receiver2.getNickname())));
    }

}
