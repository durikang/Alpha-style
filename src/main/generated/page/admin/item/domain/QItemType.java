package page.admin.item.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QItemType is a Querydsl query type for ItemType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemType extends EntityPathBase<ItemType> {

    private static final long serialVersionUID = -1862975974L;

    public static final QItemType itemType = new QItemType("itemType");

    public final StringPath code = createString("code");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QItemType(String variable) {
        super(ItemType.class, forVariable(variable));
    }

    public QItemType(Path<? extends ItemType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QItemType(PathMetadata metadata) {
        super(ItemType.class, metadata);
    }

}

