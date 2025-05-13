package com.samsamhajo.deepground.media;

import com.samsamhajo.deepground.global.utils.GlobalLogger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MediaUtils {

    private static final String MEDIA_DIR = System.getProperty("user.dir") + "/media/";

    public static InputStreamResource getMedia(String mediaUrl) {
        String[] parts = mediaUrl.split("/");
        String filename = parts[parts.length - 1];
        String fullPath = MEDIA_DIR + filename;
        
        try {
            return new InputStreamResource(new FileInputStream(new File(fullPath)));
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

    public static String generateMediaUrl(MultipartFile file) {
        // 폴더 생성 확인
        new File(MEDIA_DIR).mkdirs();
        
        String filename = generateRandomString(10) + "_" + file.getOriginalFilename();
        String fullPath = MEDIA_DIR + filename;
        
        // saveMedia 메서드 활용하여 파일 저장
        saveMedia(file, fullPath);
        
        return "/media/" + filename; // URL 용도
    }

    public static void saveMedia(MultipartFile file, String mediaPath) {
        try {
            File dest = new File(mediaPath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
        } catch (Exception e) {
            GlobalLogger.error(e.getMessage());
            throw new MediaException(MediaErrorCode.MEDIA_SAVE_ERROR);
        }
    }

    public static String getExtension(String url) {
        return url.substring(url.lastIndexOf(".") + 1);
    }

    public static String getExtension(MultipartFile multipartFile) {
        String name = multipartFile.getOriginalFilename();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static boolean deleteMedia(String mediaUrl) {
        String[] mediaUrlParts = mediaUrl.split("/");
        String mediaName = mediaUrlParts[mediaUrlParts.length - 1];
        String mediaPath = MEDIA_DIR + mediaName;  // MEDIA_DIR 상수 사용

        File file = new File(mediaPath);
        return file.exists() && file.delete();
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
