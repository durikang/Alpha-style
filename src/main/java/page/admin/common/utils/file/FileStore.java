package page.admin.common.utils.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.item.domain.UploadFile;
import page.admin.common.utils.exception.FileProcessingException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    private static final int MAX_THUMBNAILS = 4; // 최대 썸네일 수

    // 파일 저장 경로 반환
    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    // 업로드 파일 URL 반환
    public String getFileUrl(UploadFile uploadFile) {
        if (uploadFile == null || uploadFile.getStoreFileName() == null) {
            return null; // 파일 정보가 없을 경우 null 반환
        }
        return "/files/" + uploadFile.getStoreFileName();
    }


    // 단일 파일 저장
    public UploadFile storeFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        try {
            ensureDirectoryExists();
            multipartFile.transferTo(new File(getFullPath(storeFileName)));
            return new UploadFile(originalFilename, storeFileName);
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", originalFilename, e);
            throw new FileProcessingException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    // 다중 파일 저장
    public Set<UploadFile> storeFiles(Set<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            log.warn("No files to store.");
            return Collections.emptySet();
        }

        Set<UploadFile> storedFiles = new HashSet<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                log.warn("Empty file skipped: {}", file.getOriginalFilename());
                continue;
            }

            log.info("Storing file: {}", file.getOriginalFilename());
            UploadFile storedFile = storeFile(file);
            if (storedFile != null) {
                storedFiles.add(storedFile);
                log.info("File stored successfully: {}", storedFile.getStoreFileName());
            } else {
                log.error("Stored file is null for: {}", file.getOriginalFilename());
            }
        }
        return storedFiles;
    }



    // 파일 교체
    public UploadFile replaceFile(UploadFile existingFile, MultipartFile newFile) {
        try {
            if (newFile != null && !newFile.isEmpty()) {
                // 기존 파일 삭제
                if (existingFile != null) {
                    deleteFile(existingFile.getStoreFileName());
                }
                return storeFile(newFile);
            }
        } catch (FileProcessingException e) {
            log.error("파일 교체 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
        return existingFile;
    }

    // 다중 파일 교체
    public Set<UploadFile> replaceFiles(Set<UploadFile> existingFiles, Set<MultipartFile> newFiles) {
        try {
            // 새로운 파일 저장
            Set<UploadFile> updatedFiles = storeFiles(newFiles);

            // 기존 파일 삭제
            if (existingFiles != null) {
                for (UploadFile existingFile : existingFiles) {
                    deleteFile(existingFile.getStoreFileName());
                }
            }

            return updatedFiles;
        } catch (FileProcessingException e) {
            log.error("다중 파일 교체 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }


    // 파일 삭제
    public boolean deleteFile(String storeFileName) {
        if (storeFileName == null || storeFileName.isEmpty()) {
            log.warn("파일명이 비어 있습니다.");
            return true; // 파일 이름이 없으면 삭제 성공으로 간주
        }
        File file = new File(getFullPath(storeFileName));
        if (!file.exists()) {
            log.warn("파일이 존재하지 않습니다: {}", file.getAbsolutePath());
            return true; // 파일이 없어도 삭제 성공으로 간주
        }
        if (file.delete()) {
            log.info("파일 삭제 성공: {}", file.getAbsolutePath());
            return true;
        } else {
            log.error("파일 삭제 실패: {}", file.getAbsolutePath());
            return false;
        }
    }
    // 파일명 생성
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    // 저장 디렉토리 확인 및 생성
    private void ensureDirectoryExists() {
        File directory = new File(fileDir);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                log.info("디렉토리 생성: {}", directory.getPath());
            } else {
                throw new FileProcessingException("파일 저장 디렉토리를 생성할 수 없습니다: " + fileDir);
            }
        }
    }

    public String getFileDir() {
        return fileDir;
    }
}