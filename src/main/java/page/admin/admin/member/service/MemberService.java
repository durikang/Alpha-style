package page.admin.admin.member.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.domain.dto.UpdateForm;
import java.util.List;
import java.util.Optional;

public interface MemberService {
    Optional<Member> findByUserIdAndPassword(String userId, String password);
    List<Member> getAllMembers();
    Member getMemberById(Long userNo);
    Member saveMember(Member member);
    void deleteMember(Long userNo);
    Optional<Member> findByUserId(String userId);
    /**
     * 회원 정보 수정 폼을 위한 DTO 반환
     * @param userNo
     * @return UpdateForm
     */
    UpdateForm getUpdateFormById(Long userNo);
    /**
     *  관리자 페이지 회원 정보 수정
     * @param userNo
     * @param updateForm
     */
    void editMemberInfo(Long userNo, UpdateForm updateForm);

    Page<Member> searchMembers(String keyword, Pageable pageable);


    List<Member> findByEmail(String email);
}
