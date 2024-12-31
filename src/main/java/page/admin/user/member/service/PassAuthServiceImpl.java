package page.admin.user.member.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PassAuthServiceImpl implements PassAuthService {

    private final RestTemplate restTemplate;

    public PassAuthServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String sendPassAuthRequest(String phoneNumber) {
        String url = "https://api.pass-auth.com/request"; // PASS API 엔드포인트 (예시)

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer YOUR_API_KEY"); // 실제 API Key로 대체

        // 요청 바디 설정
        Map<String, String> body = new HashMap<>();
        body.put("phoneNumber", phoneNumber);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // API 호출
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        // 응답 처리
        if (response.getStatusCode().is2xxSuccessful()) {
            return "PASS 인증 요청이 성공적으로 처리되었습니다.";
        } else {
            throw new RuntimeException("PASS 인증 요청 실패: " + response.getBody());
        }
    }
}
