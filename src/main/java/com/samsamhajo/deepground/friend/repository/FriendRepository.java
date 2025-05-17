package com.samsamhajo.deepground.friend.repository;

import com.samsamhajo.deepground.friend.entity.Friend;

import com.samsamhajo.deepground.friend.entity.FriendStatus;
import com.samsamhajo.deepground.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FriendRepository extends JpaRepository<Friend,Long> {

    boolean existsByRequestMemberAndReceiveMember(Member requester, Member receiver);

    boolean existsByRequestMemberAndReceiveMemberAndStatus(Member requester, Member receiver, FriendStatus status);

    @Query("SELECT f FROM Friend f WHERE f.requestMember.id = :requesterId AND f.status = 'REQUEST'")
    List<Friend> findSentRequests(@Param("requesterId") Long requesterId);

    boolean existsByIdAndReceiveMemberAndStatus(Long id, Member receiveMember, FriendStatus status);
}
