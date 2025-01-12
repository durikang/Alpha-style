package page.admin.admin.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import page.admin.common.BaseEntity;
import page.admin.admin.member.domain.Member;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_generator")
    @SequenceGenerator(name = "order_seq_generator", sequenceName = "order_seq", allocationSize = 1)
    private Long orderNo;

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private Member user;

    private LocalDateTime orderDate;

    private Double totalAmount;

    private String deliveryStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void addOrderDetail(OrderDetail detail) {
        detail.setOrder(this);
        this.orderDetails.add(detail);
    }

    // 날짜 포맷팅 메서드 추가
    public String getFormattedOrderDate(String pattern) {
        if (this.orderDate == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return this.orderDate.format(formatter);
    }


}
