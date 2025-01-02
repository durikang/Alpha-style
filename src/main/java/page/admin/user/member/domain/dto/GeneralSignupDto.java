package page.admin.user.member.domain.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GeneralSignupDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 16, message = "아이디는 4~16자로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영문자 또는 숫자만 입력 가능합니다.")
    private String userId;

    @NotBlank(message = "이름을 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 4, max = 16, message = "비밀번호는 8~16자로 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "휴대전화 번호를 입력해주세요.")
    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$", message = "휴대전화 번호는 010-xxxx-xxxx 형식으로 입력해주세요.")
    private String mobilePhone;

    @NotBlank(message = "우편번호를 입력해주세요.")
    private String zipCode;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    private String secondaryAddress;

    @NotNull(message = "생년월일을 입력해주세요.")
    private String birthDate;

    @NotBlank(message = "보안 질문을 선택해주세요.")
    private String securityQuestion;

    @NotBlank(message = "보안 답변을 입력해주세요.")
    private String securityAnswer;
}