package page.mapper;

import org.apache.ibatis.annotations.Mapper;
import page.admin.member.domain.Member;

import java.util.List;

@Mapper
public interface MemberMapper {
    void save(Member member);
    Member findById(Long userNo);
    List<Member> findAll();
    void update(Member member);
    void delete(Long userNo);
}
