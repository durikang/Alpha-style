package page.admin.item.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
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
    @CollectionTable(name = "item_thumbnails", joinColumns = @JoinColumn(name = "item_id"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL) // Hibernate용 Cascade 설정 추가
    private List<UploadFile> thumbnails;

    private Boolean open;

    @ManyToMany(cascade = CascadeType.ALL) // 부모 삭제 시 매핑도 삭제
    @JoinTable(
            name = "item_region_mapping",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id")
    )
    private List<Region> regions;

    @ManyToOne
    @JoinColumn(name = "item_type_id")
    private ItemType itemType;

    @ManyToOne
    @JoinColumn(name = "delivery_code_id")
    private DeliveryCode deliveryCode;

    @ManyToOne
    @JoinColumn(name = "user_no")
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
