package page.admin.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import page.admin.member.domain.Member;

import java.util.List;

@Mapper
public interface MemberMapper {
    List<Member> searchMembersWithPagination(
            @Param("keyword") String keyword,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection
    );

    int countMembers(@Param("keyword") String keyword);
}
