package com.samsamhajo.deepground.Friend.FriendService;

import com.samsamhajo.deepground.friend.Dto.FriendDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class FriendReceiveTest {

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


    @BeforeEach
    void setup() {
        requester = Member.createLocalMember("paka@gamil.com", "pw", "파카");
        requester2 = Member.createLocalMember("gar@gmail.com", "pw", "가");
        requester3 = Member.createLocalMember("den@gmail.com", "pw", "든");
        receiver = Member.createLocalMember("garden@gmail.com", "pw", "가든");



        memberRepository.save(requester);
        memberRepository.save(requester3);
        memberRepository.save(requester2);
        memberRepository.save(receiver);

    }

    @Test
    public void 받은_친구_요청_목록() throws Exception {
        //given
        friendService.sendFriendRequest(requester.getId(), receiver.getEmail());
        friendService.sendFriendRequest(requester2.getId(), receiver.getEmail());
        friendService.sendFriendRequest(requester3.getId(), receiver.getEmail());
        //when
        List<FriendDto> receiveList = friendService.findFriendReceive(receiver.getId());
        //then
        assertEquals(3, receiveList.size());


        assertTrue(receiveList.stream().anyMatch(f -> f.getOtherMemberName().equals(requester.getNickname())));
        assertTrue(receiveList.stream().anyMatch(f -> f.getOtherMemberName().equals(requester2.getNickname())));
        assertTrue(receiveList.stream().anyMatch(f -> f.getOtherMemberName().equals(requester3.getNickname())));

    }


}
