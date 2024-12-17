package page.admin.member.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "members") // 테이블 이름 설정
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @SequenceGenerator(name = "member_seq_generator", sequenceName = "member_seq", allocationSize = 1)
    private Long userNo; // 사용자 번호 (PK)

    @Column(nullable = false, unique = true)
    private String userId; // 사용자 ID

    @Column(nullable = false)
    private String username; // 사용자 이름

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String email; // 이메일

    @Column(name = "mobile_phone")
    private String mobilePhone; // 휴대전화

    @Column(nullable = false)
    private String address; // 기본 주소

    @Column(name = "zip_code")
    private String zipCode; // 우편번호

    @Column(name = "secondary_address")
    private String secondaryAddress; // 상세 주소 (나머지 주소)

    @Column(name = "birth_date")
    private LocalDate birthDate; // 생년월일

    @Column(name = "security_question")
    private String securityQuestion; // 비밀번호 확인 질문

    @Column(name = "security_answer")
    private String securityAnswer; // 비밀번호 확인 답변

    @Column(nullable = false)
    private String role; // 역할 (admin, customer)
}
