package page.admin.admin.item.domain.dto;

import lombok.Getter;
import page.admin.admin.item.domain.*;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;

@Getter
public class ItemViewForm {

    private final Long itemId;
    private final String itemName;
    private final Integer purchasePrice;
    private final Integer salePrice;
    private final Integer quantity;
    private final Boolean open;
    private final Set<Region> regions;
    private final ItemType itemType;
    private final DeliveryCode deliveryCode;
    private final MainCategory mainCategory;
    private final SubCategory subCategory;
    private final UploadFile mainImage;
    private final Set<UploadFile> thumbnails;

    public ItemViewForm(Long itemId, String itemName, Integer purchasePrice, Integer salePrice, Integer quantity,
                        Boolean open, Set<Region> regions, ItemType itemType, DeliveryCode deliveryCode,
                        MainCategory mainCategory, SubCategory subCategory, UploadFile mainImage,
                        Set<UploadFile> thumbnails) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.quantity = quantity;
        this.open = open;
        this.regions = regions;
        this.itemType = itemType;
        this.deliveryCode = deliveryCode;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.mainImage = mainImage;
        this.thumbnails = thumbnails;
    }

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
}
