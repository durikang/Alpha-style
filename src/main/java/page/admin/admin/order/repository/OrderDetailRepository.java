package page.admin.admin.order.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.repository.custom.OrderDetailCustomRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>, OrderDetailCustomRepository {
    // 상품별 주문 상세 목록 (페이징)
    Page<OrderDetail> findByItem_ItemId(Long itemId, Pageable pageable);



}
