package com.gondo.map.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class FileManager {

    @Value("${file.upload-dir}")
    private String uploadDir;
    private final static String SPLIT_CHAR = "|";
    String[] imageExtensions = new String[]{"jpg", "jpeg", "png", "gif", "bmp", "webp"};


    public File getFile(String id, String fileName) {
        return new File(uploadDir + File.separator + id + SPLIT_CHAR + fileName);
    }

    public File findFileById(String id) {
        return FileUtils.listFiles(new File(uploadDir), imageExtensions, false)
                .stream()
                .filter(file -> file.getName().contains(id))
                .findFirst()
                .orElse(null);
    }

    public void storeFile(String id, MultipartFile newFile) {
        validationUploadDir(); // 업로드 폴더 검증
        try {
            File originFile = findFileById(id); // 원본 파일 검색
            if (originFile == null) {// 원본 없음.
                if (isValidMultipartFile(newFile)) {
                    // 신규 파일 저장
                    storeNewFile(id, newFile);
                }
            } else {// 원본 있음
                // 기존 파일 삭제
                FileUtils.forceDelete(originFile);
                if (isValidMultipartFile(newFile)) {
                    // 신규 파일 저장
                    storeNewFile(id, newFile);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void storeNewFile(String id, MultipartFile newFile) {
        try {
            // 신규 파일 저장
            File newDest = new File(uploadDir, findFileName(id, newFile.getOriginalFilename()));
            FileUtils.copyInputStreamToFile(newFile.getInputStream(), newDest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void validationUploadDir() {
        try {
            if (!Files.exists(Path.of(uploadDir))) {
                Files.createDirectory(Path.of(uploadDir));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // 원본 객체의 fileName 반환
    private String findFileName(String id, String fileName) {
        return id.concat(SPLIT_CHAR).concat(fileName);
    }

    // 원본 객체의 fileName 반환
    private String findServerFileName(String id, String fileName) {
        return uploadDir.concat(File.separator)
                .concat(id)
                .concat(SPLIT_CHAR)
                .concat(fileName);
    }

    public static boolean isValidMultipartFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    public void deleteFile(String id) {
        try {
            File originFile = findFileById(id);
            if (originFile != null) {
                FileUtils.forceDelete(originFile);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
