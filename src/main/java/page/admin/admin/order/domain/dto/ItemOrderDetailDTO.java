package page.admin.admin.order.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Data
public class ItemOrderDetailDTO {

    private Long orderNo;           // 주문 번호
    private LocalDateTime orderDate;         // 주문 일자
    private Long itemId;            // 상품 번호
    private String itemName;        // 상품명
    private Integer quantity;       // 수량
    private Long subtotal;          // 공급가액 (세전)
    private Double vat;             // 부가세
    private String deliveryStatus;  // 배송 상태

    // QueryDSL에서 DTO 생성 시 사용되는 생성자
    @QueryProjection
    public ItemOrderDetailDTO(Long orderNo,
                              LocalDateTime orderDate, // LocalDateTime으로 수정
                              Long itemId,
                              String itemName,
                              Integer quantity,
                              Long subtotal,
                              Double vat,
                              String deliveryStatus) {
        this.orderNo = orderNo;
        this.orderDate = orderDate; // LocalDateTime으로 수정
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.vat = vat;
        this.deliveryStatus = deliveryStatus;
    }

    /**
     * 세금 포함 총 금액: subtotal + (vat 반올림)
     */
    public Long getTotalAmount() {
        if (subtotal == null) return 0L;
        long vatAmount = (vat != null) ? Math.round(vat) : 0;
        return subtotal + vatAmount;
    }

    /**
     * 주문 일자 포맷팅 (예: yyyy-MM-dd HH:mm)
     * 뷰 단에서 thymeleaf #dates.format() 을 써도 되지만,
     * DTO에서 제공할 수도 있음 (취향/프로젝트 정책에 따라)
     */
    public String getFormattedOrderDate() {
        if (orderDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return orderDate.format(formatter);
    }

    /**
     * 공급가액 포맷팅 (원화 콤마)
     */
    public String getFormattedSubtotal() {
        if (subtotal == null) {
            return "0 원";
        }
        NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
        return nf.format(subtotal) + " 원";
    }

    /**
     * 부가세 포맷팅 (원화 콤마)
     */
    public String getFormattedVAT() {
        if (vat == null) {
            return "0 원";
        }
        NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
        // 소수점 반올림을 위해 long 변환
        long vatAmount = Math.round(vat);
        return nf.format(vatAmount) + " 원";
    }

    /**
     * 세금 포함 총 금액 포맷팅 (원화 콤마)
     */
    public String getFormattedTotalAmount() {
        NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
        return nf.format(getTotalAmount()) + " 원";
    }
}
