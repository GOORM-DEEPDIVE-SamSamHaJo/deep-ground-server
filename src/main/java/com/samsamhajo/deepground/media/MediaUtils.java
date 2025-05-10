package com.samsamhajo.deepground.media;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MediaUtils {

    public static InputStreamResource getMedia(String mediaUrl) {
        String[] mediaUrlParts = mediaUrl.split("/");
        String mediaName = mediaUrlParts[mediaUrlParts.length - 1];
        String mediaPath = "/media/" + mediaName;

        try {
            File file = new File(mediaPath);
            return new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new MediaException(MediaErrorCode.MEDIA_NOT_FOUND);
        }
    }

    public static MediaType getMediaType(String filename) {
        String ext = getExtension(filename).toLowerCase();

        return switch (ext) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    public static String generateMediaUrl(MultipartFile multipartFile) {
        String mediaName = generateRandomString(10) + "_" + multipartFile.getName();
        return "/media/" + mediaName;
    }

    public static String getExtension(String url) {
        return url.substring(url.lastIndexOf(".") + 1);
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
