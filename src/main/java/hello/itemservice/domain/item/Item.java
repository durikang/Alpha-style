package hello.itemservice.domain.item;

import hello.itemservice.domain.item.form.ItemSaveForm;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class Item {

    private Long id;
    private String itemName;
    private Long price;
    private Integer quantity;

    private UploadFile mainImage; // 메인 이미지 (단일 파일)
    private List<UploadFile> thumbnails; // 썸네일 이미지 (다중 파일 리스트)

    private Boolean open; //판매 여부
    private List<Region> regions; // Region 객체 리스트
    private ItemType itemType; //상품 종류
    private String deliveryCode; //배송 방식

    public Item() {
    }

    public Item(String itemName, Long price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

    // ItemSaveForm 기반 생성자
    public Item(String itemName, Long price, Integer quantity, Boolean open,
                List<Region> regions, ItemType itemType, String deliveryCode,
                UploadFile mainImage, List<UploadFile> thumbnails) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.open = open;
        this.regions = regions;
        this.itemType = itemType;
        this.deliveryCode = deliveryCode;
        this.mainImage = mainImage;
        this.thumbnails = thumbnails;
    }
}
