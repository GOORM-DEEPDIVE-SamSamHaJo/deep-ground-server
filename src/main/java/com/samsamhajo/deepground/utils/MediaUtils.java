package com.samsamhajo.deepground.utils;

import org.springframework.web.multipart.MultipartFile;

public class MediaUtils {
    public static String generateMediaUrl(MultipartFile multipartFile) {
        String mediaName = generateRandomString(10)+"_"+multipartFile.getName();
        return "/media/" + mediaName;
    }

    public static String getExtension(MultipartFile multipartFile) {
        String name = multipartFile.getOriginalFilename();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
