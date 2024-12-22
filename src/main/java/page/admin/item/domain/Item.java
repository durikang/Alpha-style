package page.admin.item.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
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
    private Long itemId;

    private String itemName;
    private Long price;
    private Integer quantity;

    @Embedded
    private UploadFile mainImage;

    @ElementCollection
    @CollectionTable(
            name = "item_thumbnails",
            joinColumns = @JoinColumn(name = "item_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    )
    @Cascade(CascadeType.ALL)
    private List<UploadFile> thumbnails;


    private Boolean open;

    // Regions 관계: Cascade를 제거하여 Item 삭제 시 영향 방지
    @ManyToMany
    @JoinTable(
            name = "item_region_mapping",
            joinColumns = @JoinColumn(name = "item_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "region_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    )
    private List<Region> regions;


    // ItemType 관계: nullable 허용, Cascade 설정 제거
    @ManyToOne
    @JoinColumn(name = "item_type_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ItemType itemType;

    @ManyToOne
    @JoinColumn(name = "delivery_code_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private DeliveryCode deliveryCode;

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member seller;

    // 생성자
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
                UploadFile mainImage, List<UploadFile> thumbnails, Member seller) {
        this(itemName, price, quantity, open, regions, itemType, deliveryCode, mainImage, thumbnails);
        this.seller = seller;
    }
}
