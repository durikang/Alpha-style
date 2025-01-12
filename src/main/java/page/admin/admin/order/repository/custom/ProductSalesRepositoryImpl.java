package page.admin.admin.order.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import page.admin.admin.item.domain.Item;
import page.admin.admin.member.domain.Member;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.domain.dto.ProductSalesDTO;

import java.time.LocalDateTime;
import java.util.List;

import static page.admin.admin.member.domain.QMember.member;

public class ProductSalesRepositoryImpl implements ProductSalesRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ProductSalesDTO> findProductSales(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // JPQL Query
        String jpql = "SELECT new page.admin.admin.order.domain.dto.ProductSalesDTO(" +
                "o.orderNo, o.orderDate, i.itemName, m.username, od.quantity, i.salePrice, " +
                "CAST(od.quantity * i.salePrice * 0.1 AS double), " +
                "CAST(od.quantity * i.salePrice AS double), " +
                "o.deliveryStatus) " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "JOIN od.item i " +
                "JOIN i.seller m " +
                "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
                "ORDER BY (od.quantity * i.salePrice) DESC";

        // Query 실행
        TypedQuery<ProductSalesDTO> query = entityManager.createQuery(jpql, ProductSalesDTO.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        // 페이징 처리
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<ProductSalesDTO> resultList = query.getResultList();

        // Count Query
        String countJpql = "SELECT COUNT(od) " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "WHERE o.orderDate BETWEEN :startDate AND :endDate";

        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        countQuery.setParameter("startDate", startDate);
        countQuery.setParameter("endDate", endDate);
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }
}

