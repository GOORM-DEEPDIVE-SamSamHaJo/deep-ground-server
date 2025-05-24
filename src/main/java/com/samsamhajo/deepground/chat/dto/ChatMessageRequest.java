package com.samsamhajo.deepground.chat.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageRequest {

    private String message;
    private List<String> mediaIds;
}
