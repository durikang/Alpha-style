package page.admin.item.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.admin.common.BaseEntity;
import page.admin.member.domain.Member;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq_generator")
    @SequenceGenerator(name = "item_seq_generator", sequenceName = "item_seq", allocationSize = 1)
    private Long id;

    private String itemName;
    private Long price;
    private Integer quantity;

    @Embedded
    private UploadFile mainImage; // 메인 이미지

    @ElementCollection
    @CollectionTable(name = "item_thumbnails", joinColumns = @JoinColumn(name = "item_id"))
    private List<UploadFile> thumbnails; // 썸네일 이미지

    private Boolean open; // 판매 여부

    // Region: 다대다 관계
    @ManyToMany
    @JoinTable(
            name = "item_region_mapping",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id")
    )
    private List<Region> regions; // 연관관계 매핑된 Region 리스트

    // ItemType: 단일 선택 (ManyToOne)
    @ManyToOne
    @JoinColumn(name = "item_type_id")
    private ItemType itemType; // 연관관계 매핑된 ItemType

    // DeliveryCode: 단일 선택 (ManyToOne)
    @ManyToOne
    @JoinColumn(name = "delivery_code_id")
    private DeliveryCode deliveryCode;

    // 판매자 (Member와 다대일 관계)
    @ManyToOne
    @JoinColumn(name = "seller_id") // 외래 키 컬럼 이름 설정
    private Member seller;

    public Item(String itemName, Long price, Integer quantity, Boolean open,
                List<Region> regions, ItemType itemType, DeliveryCode deliveryCode,
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
    public Item(String itemName, Long price, Integer quantity, Boolean open,
                List<Region> regions, ItemType itemType, DeliveryCode deliveryCode,
                UploadFile mainImage, List<UploadFile> thumbnails,Member seller) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.open = open;
        this.regions = regions;
        this.itemType = itemType;
        this.deliveryCode = deliveryCode;
        this.mainImage = mainImage;
        this.thumbnails = thumbnails;
        this.seller = seller;
    }
}
