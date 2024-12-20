package page.admin.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.member.domain.dto.MemberList;

public interface MemberRepositoryCustom {
    Page<MemberList> searchMembersWithPagination(String keyword, Pageable pageable);
}
