package page.admin.admin.item.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = 619441407L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItem item = new QItem("item");

    public final page.admin.common.QBaseEntity _super = new page.admin.common.QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final QDeliveryCode deliveryCode;

    public final NumberPath<Long> itemId = createNumber("itemId", Long.class);

    public final StringPath itemName = createString("itemName");

    public final QItemType itemType;

    public final QMainCategory mainCategory;

    public final QUploadFile mainImage;

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final BooleanPath open = createBoolean("open");

    public final NumberPath<Integer> purchasePrice = createNumber("purchasePrice", Integer.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final SetPath<Region, QRegion> regions = this.<Region, QRegion>createSet("regions", Region.class, QRegion.class, PathInits.DIRECT2);

    public final SetPath<Review, QReview> reviews = this.<Review, QReview>createSet("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public final NumberPath<Integer> salePrice = createNumber("salePrice", Integer.class);

    public final NumberPath<Long> salesCount = createNumber("salesCount", Long.class);

    public final page.admin.admin.member.domain.QMember seller;

    public final QSubCategory subCategory;

    public final SetPath<UploadFile, QUploadFile> thumbnails = this.<UploadFile, QUploadFile>createSet("thumbnails", UploadFile.class, QUploadFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public QItem(String variable) {
        this(Item.class, forVariable(variable), INITS);
    }

    public QItem(Path<? extends Item> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItem(PathMetadata metadata, PathInits inits) {
        this(Item.class, metadata, inits);
    }

    public QItem(Class<? extends Item> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.deliveryCode = inits.isInitialized("deliveryCode") ? new QDeliveryCode(forProperty("deliveryCode")) : null;
        this.itemType = inits.isInitialized("itemType") ? new QItemType(forProperty("itemType")) : null;
        this.mainCategory = inits.isInitialized("mainCategory") ? new QMainCategory(forProperty("mainCategory")) : null;
        this.mainImage = inits.isInitialized("mainImage") ? new QUploadFile(forProperty("mainImage"), inits.get("mainImage")) : null;
        this.seller = inits.isInitialized("seller") ? new page.admin.admin.member.domain.QMember(forProperty("seller")) : null;
        this.subCategory = inits.isInitialized("subCategory") ? new QSubCategory(forProperty("subCategory"), inits.get("subCategory")) : null;
    }

}

