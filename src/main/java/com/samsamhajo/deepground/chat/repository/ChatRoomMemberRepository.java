package com.samsamhajo.deepground.chat.repository;

import com.samsamhajo.deepground.chat.entity.ChatRoomMember;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    Optional<ChatRoomMember> findByMemberIdAndChatRoomId(Long memberId, Long chatRoomId);

    boolean existsByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);

    @Modifying
    @Query("UPDATE ChatRoomMember SET deleted = true WHERE chatRoom.id = :chatRoomId")
    void softDeleteByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}
