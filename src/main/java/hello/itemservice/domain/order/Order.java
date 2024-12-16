package hello.itemservice.domain.order;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.utils.CommonDataProvider;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Order {
    private Long orderNo;              // 주문 번호
    private Long userNo;               // 사용자 번호 (외래 키)
    private Date orderDate;            // 주문 날짜
    private Double totalAmount;        // 총 금액
    private String delivaryStatus;             // 배송 상태
    private List<OrderDetail> details; // 주문 상세 목록

    public String getDisplayName() {
        DeliveryCode deliveryCode = CommonDataProvider.getDeliveryStatuses().stream()
                .filter(status -> status.getCode().equals(this.delivaryStatus))
                .findFirst()
                .orElse(null);
        return deliveryCode != null ? deliveryCode.getDisplayName() : "알 수 없음";
    }

}