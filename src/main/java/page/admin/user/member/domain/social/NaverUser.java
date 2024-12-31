package page.admin.user.member.domain.social;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.admin.admin.member.domain.Member;

@Entity
@Data
@NoArgsConstructor
@Table(name = "naver_users")
public class NaverUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String providerId; // 네이버 API에서 제공하는 고유 ID

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String mobilePhone;

    @Column(nullable = true)
    private String username;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 회원 엔티티와의 관계
}
