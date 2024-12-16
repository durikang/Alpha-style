package hello.itemservice.domain.users;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Member {
    private Long userNo; // 사용자 번호


    private String userId; // 사용자 ID


    private String username; // 사용자 이름


    private String password; // 비밀번호

    private String email; // 이메일

    private String mobilePhone; // 휴대전화

    private String address; // 기본 주소

    private String zipCode; // 우편번호

    private String secondaryAddress; // 상세 주소 (나머지 주소)

    private LocalDate birthDate; // 생년월일

    private String securityQuestion; // 비밀번호 확인 질문

    private String securityAnswer; // 비밀번호 확인 답변

    private String role; // 역할 (admin, customer)
}
