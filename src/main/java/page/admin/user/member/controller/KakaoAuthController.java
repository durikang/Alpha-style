package page.admin.user.member.controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import page.admin.common.utils.email.CodeGenerator;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/kakao")
public class KakaoAuthController {

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String MESSAGE_SEND_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private static final String CLIENT_ID = "39815c571f5c31204d0ba21bc705dca8";
    private static final String REDIRECT_URI = "http://localhost:9090/kakao/callback";

    // 인증 코드를 메모리에 저장 (실제 환경에서는 Redis 등을 활용 권장)
    private final Map<String, String> authCodeMap = new HashMap<>();

    @GetMapping("/auth-url")
    public ResponseEntity<String> getKakaoAuthUrl() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&scope=talk_message"; // 메시지 전송 권한 추가

        return ResponseEntity.ok(kakaoAuthUrl);
    }

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

            // 토큰 요청 응답 검증
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                return ResponseEntity.status(500).body("카카오 인증 실패: " + response.getStatusCode());
            }

            String accessToken = (String) response.getBody().get("access_token");

            // 2. 사용자 정보 요청
            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.add("Authorization", "Bearer " + accessToken);

            HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);
            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, userInfoRequest, Map.class);

            if (userInfoResponse.getStatusCode() != HttpStatus.OK || userInfoResponse.getBody() == null) {
                return ResponseEntity.status(500).body("카카오 사용자 정보 요청 실패");
            }

            Map<String, Object> userInfo = userInfoResponse.getBody();
            String userId = String.valueOf(userInfo.get("id"));

            // "properties" 키가 존재하는지 확인하고 처리
            Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
            String nickname = (properties != null && properties.containsKey("nickname"))
                    ? String.valueOf(properties.get("nickname"))
                    : "알 수 없는 사용자";

            // 3. 인증 코드 생성 및 저장
            String authCode = CodeGenerator.generateCode();
            authCodeMap.put(userId, authCode);

            // 4. 카카오톡 메시지 전송
            sendKakaoMessage(accessToken, "인증 코드: " + authCode);

            return ResponseEntity.ok("카카오 인증 성공: 사용자 ID - " + userId + ", 닉네임 - " + nickname);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("카카오 인증 실패: " + e.getMessage());
        }
    }

    // 새로운 엔드포인트: 카카오톡으로 인증 코드 전송
    @PostMapping("/send-code")
    public ResponseEntity<Map<String, Object>> sendAuthCode(@RequestParam("userId") String userId, @RequestParam("accessToken") String accessToken) {
        try {
            // 인증 코드 생성 및 저장
            String authCode = CodeGenerator.generateCode();
            authCodeMap.put(userId, authCode);

            // 카카오톡 메시지 전송
            sendKakaoMessage(accessToken, "인증 코드: " + authCode);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "카카오톡으로 인증 코드가 전송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "카카오톡 메시지 전송 실패");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // 인증 코드 확인 API
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyAuthCode(@RequestParam("userId") String userId, @RequestParam("authCode") String authCode) {
        if (authCodeMap.containsKey(userId) && authCodeMap.get(userId).equals(authCode)) {
            return ResponseEntity.ok("인증 성공");
        }
        return ResponseEntity.status(400).body("인증 실패: 인증 코드가 일치하지 않습니다.");
    }

    private void sendKakaoMessage(String accessToken, String message) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> templateObject = new HashMap<>();
            templateObject.put("object_type", "text");
            templateObject.put("text", message);
            templateObject.put("link", Map.of("web_url", REDIRECT_URI));

            Map<String, Object> body = new HashMap<>();
            body.put("template_object", templateObject);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(MESSAGE_SEND_URL, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("카카오톡 메시지 전송 실패");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("카카오톡 메시지 전송 중 오류 발생");
        }
    }
}
