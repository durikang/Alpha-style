package page.flaskapi.file.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFilesToFlask(@RequestParam("files") List<MultipartFile> files) {
        try {
            // Flask API URL
            String flaskUrl = "http://127.0.0.1:5000/upload";

            // HTTP 요청 생성
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 파일 데이터를 MultiValueMap에 추가
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            for (MultipartFile file : files) {
                body.add("files", new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                });
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Flask API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, requestEntity, String.class);
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
