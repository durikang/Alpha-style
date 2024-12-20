package page.admin.item.domain;

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

    private static final long serialVersionUID = -669476160L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItem item = new QItem("item");

    public final page.admin.common.QBaseEntity _super = new page.admin.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final QDeliveryCode deliveryCode;

    public final NumberPath<Long> itemId = createNumber("itemId", Long.class);

    public final StringPath itemName = createString("itemName");

    public final QItemType itemType;

    public final QUploadFile mainImage;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final BooleanPath open = createBoolean("open");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final ListPath<Region, QRegion> regions = this.<Region, QRegion>createList("regions", Region.class, QRegion.class, PathInits.DIRECT2);

    public final page.admin.member.domain.QMember seller;

    public final ListPath<UploadFile, QUploadFile> thumbnails = this.<UploadFile, QUploadFile>createList("thumbnails", UploadFile.class, QUploadFile.class, PathInits.DIRECT2);

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
        this.mainImage = inits.isInitialized("mainImage") ? new QUploadFile(forProperty("mainImage")) : null;
        this.seller = inits.isInitialized("seller") ? new page.admin.member.domain.QMember(forProperty("seller")) : null;
    }

}

