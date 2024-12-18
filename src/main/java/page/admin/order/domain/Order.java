package page.admin.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import page.admin.common.BaseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders") // 테이블 이름을 'orders'로 변경
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_generator")
    @SequenceGenerator(name = "order_seq_generator", sequenceName = "order_seq", allocationSize = 1)
    private Long orderNo; // 주문 번호 (PK)

    private Long userNo; // 사용자 번호

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate; // 주문 날짜

    private Double totalAmount; // 총 금액

    private String deliveryStatus; // 배송 상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void addOrderDetail(OrderDetail detail) {
        detail.setOrder(this); // 연관관계 설정
        this.orderDetails.add(detail);
    }
}
