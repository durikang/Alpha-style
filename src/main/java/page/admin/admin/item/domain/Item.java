package page.admin.admin.item.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import page.admin.common.BaseEntity;
import page.admin.admin.member.domain.Member;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // BaseEntity의 equals, hashCode를 호출
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @SequenceGenerator(name = "item_seq", sequenceName = "item_seq", allocationSize = 1)
    private Long itemId;

    @Column(nullable = false)
    private String itemName; // 상품명

    @Column(nullable = false)
    private Integer purchasePrice; // 매입가

    @Column(nullable = false)
    private Integer salePrice; // 판매가

    @Column(nullable = false)
    private Integer quantity; // 수량

    @Column(nullable = false)
    private Boolean open; // 판매 여부

    @CreationTimestamp
    private LocalDateTime createdDate; // 생성 날짜

    @UpdateTimestamp
    private LocalDateTime modifiedDate; // 수정 날짜

    // 메인 이미지
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "main_image_id", referencedColumnName = "id")
    private UploadFile mainImage;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "item_id")
    private Set<UploadFile> thumbnails;

    // 지역 (N:N 관계)
    @ManyToMany
    @JoinTable(
            name = "item_region_mapping",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id")
    )
    private Set<Region> regions;

    // 상품 종류 (1:N 관계)
    @ManyToOne
    @JoinColumn(name = "item_type_id", nullable = false)
    private ItemType itemType;

    // 배송 방식 (1:N 관계)
    @ManyToOne
    @JoinColumn(name = "delivery_code_id", nullable = false)
    private DeliveryCode deliveryCode;

    // 메인 카테고리
    @ManyToOne
    @JoinColumn(name = "main_category_id", nullable = false)
    private MainCategory mainCategory;

    // 세부 카테고리
    @ManyToOne
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    // 판매자 정보 (N:1 관계)
    @ManyToOne
    @JoinColumn(name = "seller_user_no", referencedColumnName = "userNo", nullable = false)
    private Member seller;

    // 생성자
    public Item(String itemName, Integer purchasePrice, Integer salePrice, Integer quantity, Boolean open,
                Set<Region> regions, ItemType itemType, DeliveryCode deliveryCode,
                UploadFile mainImage, Set<UploadFile> thumbnails, MainCategory mainCategory, SubCategory subCategory,
                Member seller) {
        this.itemName = itemName;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.quantity = quantity;
        this.open = open;
        this.regions = regions;
        this.itemType = itemType;
        this.deliveryCode = deliveryCode;
        this.mainImage = mainImage;
        this.thumbnails = thumbnails;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.seller = seller;
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

}