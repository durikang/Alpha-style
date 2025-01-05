package page.admin.admin.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSessionInfo {
    private Long userNo;      // 사용자 번호 (PK)
    private String userId;    // 사용자 로그인 ID
    private String username;  // 사용자 이름
    private String role;      // 사용자 역할
    private String email;     // 이메일
    private String gender;    // 성별
}
