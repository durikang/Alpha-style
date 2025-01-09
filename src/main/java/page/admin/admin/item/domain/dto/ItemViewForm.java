package page.admin.admin.item.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.admin.admin.item.domain.*;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemViewForm {

    private Long itemId;
    private String itemName;
    private Integer purchasePrice;
    private Integer salePrice;
    private Integer quantity;
    private Boolean open;
    private Set<Region> regions;
    private ItemType itemType;
    private DeliveryCode deliveryCode;
    private MainCategory mainCategory;
    private SubCategory subCategory;
    private UploadFile mainImage;
    private List<UploadFile> thumbnails;
    private Long viewCount;
    private Long salesCount;
    private Double averageRating;

    // 판매 여부
    public String getOpenStatus() {
        return open != null && open ? "판매 중" : "판매 종료";
    }

    // 가격 포맷팅 메서드 추가
    public String getFormattedPurchasePrice() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);
        return purchasePrice != null ? currencyFormat.format(purchasePrice) : "가격 정보 없음";
    }

    public String getFormattedSalePrice() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);
        return salePrice != null ? currencyFormat.format(salePrice) : "가격 정보 없음";
    }
    // 상품 종류 설명 추가
    public String getItemTypeDescription() {
        return itemType != null ? itemType.getDescription() : "정보 없음";
    }
    // 배송 방식 설명 추가
    public String getDeliveryDisplayName() {
        return deliveryCode != null ? deliveryCode.getDisplayName() : "정보 없음";
    }

    // 추가된 필드에 대한 포맷팅 메서드
    public String getFormattedViewCount() {
        return String.format("%,d회", viewCount != null ? viewCount : 0);
    }

    public String getFormattedSalesCount() {
        return String.format("%,d개", salesCount != null ? salesCount : 0);
    }


    // 평균 평점 포맷팅
    public String getFormattedAverageRating() {
        if (averageRating == null) {
            return "0.0";
        }
        return String.format("%.1f", averageRating);
    }

}
