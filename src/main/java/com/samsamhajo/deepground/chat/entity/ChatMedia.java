package com.samsamhajo.deepground.chat.entity;

import com.samsamhajo.deepground.global.BaseDocument;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "chat_media")
public class ChatMedia extends BaseDocument {

    @Id
    private String id;

    @Field("chat_room_id")
    private Long chatRoomId;

    @Field("member_id")
    private Long memberId;

    @Field("media")
    private ChatMessageMedia media;

    @Field("file_status")
    private FileStatus status;

    private ChatMedia(Long chatRoomId, Long memberId, ChatMessageMedia media) {
        this.chatRoomId = chatRoomId;
        this.memberId = memberId;
        this.media = media;
        this.status = FileStatus.PENDING;
    }

    public static ChatMedia of(Long chatRoomId, Long memberId, ChatMessageMedia media) {
        return new ChatMedia(chatRoomId, memberId, media);
    }

    public void send() {
        this.status = FileStatus.SENT;
    }

    public void cancel() {
        this.status = FileStatus.CANCELED;
    }
}
