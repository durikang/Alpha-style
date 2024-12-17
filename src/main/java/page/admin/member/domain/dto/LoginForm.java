package page.admin.member.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginForm {
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
