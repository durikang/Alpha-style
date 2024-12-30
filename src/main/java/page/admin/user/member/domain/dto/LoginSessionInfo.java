package page.admin.user.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginSessionInfo {
    private Long userNo;          // 사용자 번호 (PK)
    private String userId;        // 사용자 로그인 ID
    private String username;      // 사용자 이름
    private String email;         // 이메일
    private String address;       // 주소
    private String phoneNumber;   // 전화번호
    // 기타 사용자 전용 필드
}
