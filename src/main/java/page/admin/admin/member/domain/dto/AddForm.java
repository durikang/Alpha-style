package page.admin.admin.member.domain.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddForm {

    @NotEmpty(message = "아이디는 필수 입력 항목입니다.")
    private String userId; // 아이디

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String username; // 이름

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상 입력해야 합니다.")
    private String password; // 비밀번호

    @Email(message = "유효한 이메일 주소를 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email; // 이메일

    @NotBlank(message = "휴대전화 번호는 필수 입력 항목입니다.")
    private String mobilePhone; // 휴대전화

    @NotBlank(message = "주소는 필수 입력 항목입니다.")
    private String address; // 주소

    @NotBlank(message = "우편번호는 필수 입력 항목입니다.")
    private String zipCode; // 우편번호

    private String secondaryAddress; // 상세 주소

    @NotBlank(message = "역할은 필수 입력 항목입니다.")
    private String role; // 역할
}