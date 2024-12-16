package hello.itemservice.domain.item;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 주문           ORDER
 주문완료       COMPLETED
 주문취소       CANCELED
 구매완료       PURCHASED
 교환           EXCHANGED
 반품           RETURNED
 */

@Data
@AllArgsConstructor
public class PurchaseStatus {

    private String code;
    private String status;

    // 정적 상수 선언
    public static final PurchaseStatus ORDER = new PurchaseStatus("ORDER", "주문");
    public static final PurchaseStatus COMPLETED = new PurchaseStatus("COMPLETED", "주문완료");
    public static final PurchaseStatus CANCELED = new PurchaseStatus("CANCELED", "주문취소");
    public static final PurchaseStatus PURCHASED = new PurchaseStatus("PURCHASED", "구매완료");
    public static final PurchaseStatus EXCHANGED = new PurchaseStatus("EXCHANGED", "교환");
    public static final PurchaseStatus RETURNED = new PurchaseStatus("RETURNED", "반품");
}
