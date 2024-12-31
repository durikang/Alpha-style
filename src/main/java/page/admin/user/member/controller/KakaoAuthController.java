package page.admin.user.member.controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/kakao")
public class KakaoAuthController {

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String CLIENT_ID = "your-app-client-id";
    private static final String CLIENT_SECRET = "your-app-client-secret";
    private static final String REDIRECT_URI = "https://yourdomain.com/kakao/callback";

    @GetMapping("/callback")
    public ResponseEntity<String> handleKakaoCallback(@RequestParam("code") String code) {
        try {
            // 1. 액세스 토큰 요청
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", CLIENT_ID);
            params.add("redirect_uri", REDIRECT_URI);
            params.add("code", code);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);

            String accessToken = (String) response.getBody().get("access_token");

            // 2. 사용자 정보 요청
            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.add("Authorization", "Bearer " + accessToken);

            HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);
            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, userInfoRequest, Map.class);

            // 3. 사용자 정보 반환
            Map<String, Object> userInfo = userInfoResponse.getBody();
            return ResponseEntity.ok("카카오 인증 성공: " + userInfo);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("카카오 인증 실패: " + e.getMessage());
        }
    }
}
