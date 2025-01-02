package page.admin.admin.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import page.admin.admin.member.domain.Member;


import java.util.Optional;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{
    Optional<Member> findByUserId(String userId); // 사용자 ID로 조회 (중복 검사를 위한 용도)
    Optional<Member> findByUserIdAndPassword(String userId, String password);
    // 검색 및 페이징 처리 메서드
    Page<Member> findByUserIdContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String userIdKeyword, String usernameKeyword, String emailKeyword, Pageable pageable
    );



    Optional<Member> findSingleMemberByEmail(String email); // AuthService
    List<Member> findAllMembersByEmail(String email);       // MemberService

    boolean existsByEmail(String email);
}
