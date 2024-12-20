package page.admin.member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberList {
    private Long userNo;
    private String userId;
    private String username;
    private String email;
    private String mobilePhone;
    private String role;

    public MemberList() {
        // 기본 생성자
    }

    @QueryProjection
    public MemberList(Long userNo, String userId, String username, String email, String mobilePhone, String role) {
        this.userNo = userNo;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.mobilePhone = mobilePhone;
        this.role = role;
    }
}
