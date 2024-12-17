package page.admin.item.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ItemUpdateForm {

    @NotNull
    private Long id; // 상품 ID

    @NotNull
    private String itemName; // 상품명

    @NotNull
    private Integer price; // 가격

    @NotNull
    private Integer quantity; // 수량

    private Boolean open; // 판매 여부
    private List<String> regions; // 등록 지역 (코드 리스트)
    private String itemType; // 상품 종류
    private String deliveryCode; // 배송 방식
}