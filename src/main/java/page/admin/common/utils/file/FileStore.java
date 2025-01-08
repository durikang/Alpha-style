package page.admin.common.utils.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.item.domain.UploadFile;
import page.admin.admin.manager.service.FileSettingsService;
import page.admin.common.utils.exception.FileProcessingException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    private final FileSettingsService fileSettingsService;

    // 파일 저장 경로 반환
    public String getFullPath(String filename) {
        // 경로 끝에 슬래시가 없으면 추가
        if (!fileDir.endsWith("/") && !fileDir.endsWith("\\")) {
            return fileDir + File.separator + filename;
        }
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
        if (!isValidFile(multipartFile)) {
            throw new FileProcessingException("유효하지 않은 파일입니다.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        try {
            ensureDirectoryExists();
            multipartFile.transferTo(new File(getFullPath(storeFileName)));
            log.info("파일 저장 완료: {}", storeFileName);
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
            if (file.isEmpty() || !isValidFile(file)) {
                log.warn("유효하지 않은 파일: {}", file.getOriginalFilename());
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
        // 파일명에 안전한 문자만 사용하도록 변환 (예: 알파벳, 숫자, -, _)
        String safeFilename = uuid.replaceAll("[^a-zA-Z0-9-_]", "") + "." + ext;
        return safeFilename;
    }

    // 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        if (pos == -1 || pos == originalFilename.length() - 1) {
            throw new FileProcessingException("파일 확장자를 찾을 수 없습니다: " + originalFilename);
        }
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
        if (!directory.isDirectory() || !directory.canWrite()) {
            throw new FileProcessingException("파일 저장 경로가 디렉토리가 아니거나 쓰기 권한이 없습니다: " + fileDir);
        }
    }
    // 파일 검증 메서드
    private boolean isValidFile(MultipartFile file) {
        // 설정에서 허용된 파일 확장자 가져오기
        String allowedExtensionsStr = fileSettingsService.getSettings().getAllowedExtensions();
        Set<String> allowedExtensions = Arrays.stream(allowedExtensionsStr.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // 설정에서 최대 파일 크기 가져오기
        long maxFileSize = fileSettingsService.getSettings().getMaxFileSize(); // 바이트 단위

        // 파일 이름 및 크기 확인
        if (file == null || file.isEmpty()) {
            log.warn("파일이 비어있습니다.");
            return false;
        }

        // 파일 확장자 추출 및 검증
        String originalFilename = file.getOriginalFilename();
        String extension = extractExt(originalFilename).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            log.warn("허용되지 않은 파일 형식입니다: {}", extension);
            return false;
        }

        // 파일 크기 검증
        if (file.getSize() > maxFileSize) {
            log.warn("파일 크기가 너무 큽니다. 크기: {} bytes", file.getSize());
            return false;
        }

        return true;
    }


}
