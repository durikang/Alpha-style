package page.admin.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.member.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId); // 사용자 ID로 조회 (중복 검사를 위한 용도)
    Optional<Member> findByUserIdAndPassword(String userId, String password);
}
