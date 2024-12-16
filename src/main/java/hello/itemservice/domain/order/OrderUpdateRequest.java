package hello.itemservice.domain.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateRequest {
    private Long orderNo; // 주문 번호
    private String status; // 주문 상태 추가
    private List<OrderDetailRequest> details; // 주문 상세 정보
}
