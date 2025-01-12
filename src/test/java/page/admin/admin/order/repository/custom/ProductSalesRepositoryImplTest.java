package page.admin.admin.order.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import page.admin.admin.item.domain.Item;
import page.admin.admin.member.domain.Member;
import page.admin.admin.order.domain.Order;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.domain.dto.ProductSalesDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductSalesRepositoryImplTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testFindProductSales() {
        // 테스트 데이터 준비
        createTestData();

        // CriteriaBuilder 생성
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // CriteriaQuery 생성
        CriteriaQuery<ProductSalesDTO> cq = cb.createQuery(ProductSalesDTO.class);
        Root<OrderDetail> od = cq.from(OrderDetail.class);

        // Join 설정
        Join<OrderDetail, Order> order = od.join("order");
        Join<OrderDetail, Item> item = od.join("item");
        Join<Item, Member> member = item.join("seller");

        // Select 절
        cq.select(cb.construct(
                ProductSalesDTO.class,
                od.get("orderNo"),
                item.get("itemName"),
                member.get("username"),
                od.get("quantity"),
                item.get("salePrice"),
                cb.prod(od.get("quantity"), item.get("salePrice"))
        ));

        // Where 절
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();
        cq.where(cb.between(order.get("orderDate"), startDate, endDate));

        // Order By 절
        cq.orderBy(cb.desc(cb.prod(od.get("quantity"), item.get("salePrice"))));

        // 쿼리 실행
        List<ProductSalesDTO> resultList = entityManager.createQuery(cq).getResultList();

        // 결과 검증
        assertThat(resultList).isNotEmpty();
        assertThat(resultList.get(0).getItemName()).isEqualTo("Test Item");
    }

    private void createTestData() {
        // Member 생성
        Member seller = new Member();
        seller.setUsername("TestSeller");
        entityManager.persist(seller);

        // Item 생성
        Item item = new Item();
        item.setItemName("Test Item");
        item.setSalePrice(1000);
        item.setSeller(seller);
        entityManager.persist(item);

        // Order 생성
        page.admin.admin.order.domain.Order order = new page.admin.admin.order.domain.Order();
        order.setOrderDate(LocalDateTime.now().minusDays(5));
        entityManager.persist(order);

        // OrderDetail 생성
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setItem(item);
        orderDetail.setQuantity(10);
        entityManager.persist(orderDetail);

        entityManager.flush();
        entityManager.clear();
    }
}
