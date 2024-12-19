package page.admin.member.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.admin.common.BaseEntity;
import page.admin.item.domain.Item;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "members")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @SequenceGenerator(name = "member_seq_generator", sequenceName = "member_seq", allocationSize = 1)
    private Long userNo;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(nullable = false)
    private String address;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "secondary_address")
    private String secondaryAddress;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "security_question")
    private String securityQuestion;

    @Column(name = "security_answer")
    private String securityAnswer;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true) // 연관된 Item 삭제
    private List<Item> items;
}
