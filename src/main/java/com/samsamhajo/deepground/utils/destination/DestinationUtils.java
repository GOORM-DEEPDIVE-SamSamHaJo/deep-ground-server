package com.samsamhajo.deepground.utils.destination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DestinationUtils {

    private static final Pattern CHATROOM_PATTERN = Pattern.compile("^/(topic|app)/chatrooms/(\\d+)/?.*");

    public static Long extractChatRoomId(String destination) {
        Matcher matcher = CHATROOM_PATTERN.matcher(destination);

        if (matcher.find()) {
            return Long.parseLong(matcher.group(2));
        }

        return null;
    }
}
