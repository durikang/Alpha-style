package page.admin.admin.item.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import page.admin.common.BaseEntity;
import page.admin.admin.member.domain.Member;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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

    @Column(nullable = false)
    private Long viewCount = 0L; // 조회수

    @Column(nullable = false)
    private Long salesCount = 0L; // 판매량

    // 메인 이미지
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "main_image_id", referencedColumnName = "id")
    @ToString.Exclude
    private UploadFile mainImage;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UploadFile> thumbnails = new HashSet<>();

    // 리뷰 (1:N 관계)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    // 지역 (N:N 관계)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "item_region_mapping",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id")
    )
    private Set<Region> regions = new HashSet<>();

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

    // 헬퍼 메서드 추가
    public void addThumbnail(UploadFile thumbnail) {
        thumbnails.add(thumbnail);
        thumbnail.setItem(this);
    }

    public void removeThumbnail(UploadFile thumbnail) {
        thumbnails.remove(thumbnail);
        thumbnail.setItem(null);
    }

    // 생성자 수정: thumbnails에 Item 설정
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
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.seller = seller;

        if (thumbnails != null) {
            thumbnails.forEach(this::addThumbnail);
        }
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
