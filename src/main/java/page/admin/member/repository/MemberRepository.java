package page.admin.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import page.admin.member.domain.Member;
import page.admin.member.domain.dto.MemberList;


import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> , MemberRepositoryCustom{
    Optional<Member> findByUserId(String userId); // 사용자 ID로 조회 (중복 검사를 위한 용도)
    Optional<Member> findByUserIdAndPassword(String userId, String password);
    // 검색 및 페이징 처리 메서드
    Page<Member> findByUserIdContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String userIdKeyword, String usernameKeyword, String emailKeyword, Pageable pageable
    );


}
