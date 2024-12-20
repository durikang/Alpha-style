package page.admin.member.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import page.admin.member.domain.dto.MemberList;

import java.util.List;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager entityManager;

    public MemberRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<MemberList> searchMembersWithPagination(String keyword, Pageable pageable) {
        // ROWNUM 기반 페이징 처리 SQL
        String baseSql = "SELECT * FROM ( " +
                "SELECT ROWNUM AS rn, m.user_no AS userNo, m.user_id AS userId, m.username, m.email, m.mobile_phone AS mobilePhone, m.role " +
                "FROM ( " +
                "SELECT * FROM members " +
                "WHERE UPPER(user_id) LIKE UPPER(:keyword) OR " +
                "UPPER(username) LIKE UPPER(:keyword) OR " +
                "UPPER(email) LIKE UPPER(:keyword) " +
                "ORDER BY user_no ASC " +
                ") m " +
                "WHERE ROWNUM <= :endRow " +
                ") " +
                "WHERE rn > :startRow";

        // 총 결과 수 계산 SQL
        String countSql = "SELECT COUNT(*) FROM members " +
                "WHERE UPPER(user_id) LIKE UPPER(:keyword) OR " +
                "UPPER(username) LIKE UPPER(:keyword) OR " +
                "UPPER(email) LIKE UPPER(:keyword)";

        // 키워드와 페이징 정보
        String formattedKeyword = "%" + keyword + "%";
        int startRow = (int) pageable.getOffset();
        int endRow = startRow + pageable.getPageSize();

        // 결과 데이터 쿼리 실행
        Query query = entityManager.createNativeQuery(baseSql, "MemberListMapping");
        query.setParameter("keyword", formattedKeyword);
        query.setParameter("startRow", startRow);
        query.setParameter("endRow", endRow);

        @SuppressWarnings("unchecked")
        List<MemberList> members = query.getResultList();

        // 총 결과 수 쿼리 실행
        Query countQuery = entityManager.createNativeQuery(countSql);
        countQuery.setParameter("keyword", formattedKeyword);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        // Page 객체로 반환
        return new PageImpl<>(members, pageable, total);
    }
}
