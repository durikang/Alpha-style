package page.admin.item.domain.dto;

import lombok.Getter;
import lombok.Setter;
import page.admin.item.domain.DeliveryCode;
import page.admin.item.domain.ItemType;
import page.admin.item.domain.Region;
import page.admin.item.domain.UploadFile;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
public class ItemViewForm {
    private Long itemId;             // 상품 ID
    private String itemName;         // 상품명
    private Long price;              // 가격
    private String formattedPrice;   // 포맷된 가격 (₩ 표시 포함)
    private Integer quantity;        // 수량
    private Boolean open;            // 판매 여부
    private List<Region> regions;    // 등록 지역
    private ItemType itemType;       // 상품 종류
    private DeliveryCode deliveryCode; // 배송 방식
    private UploadFile mainImage;    // 메인 이미지
    private List<UploadFile> thumbnails; // 썸네일 이미지

    public String getFormattedPrice() {
        if (price == null) return "가격 정보 없음";
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);
        return currencyFormat.format(price);
    }

    public String getDeliveryDisplayName() {
        return deliveryCode != null ? deliveryCode.getDisplayName() : "배송 정보 없음";
    }

    public String getItemTypeDescription() {
        return itemType != null ? itemType.getDescription() : "상품 종류 없음";
    }

    public String getOpenStatus() {
        return open != null && open ? "판매 중" : "판매 중지";
    }
}
