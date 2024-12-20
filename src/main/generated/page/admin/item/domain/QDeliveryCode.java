package page.admin.item.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeliveryCode is a Querydsl query type for DeliveryCode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeliveryCode extends EntityPathBase<DeliveryCode> {

    private static final long serialVersionUID = -1523235154L;

    public static final QDeliveryCode deliveryCode = new QDeliveryCode("deliveryCode");

    public final StringPath code = createString("code");

    public final StringPath displayName = createString("displayName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QDeliveryCode(String variable) {
        super(DeliveryCode.class, forVariable(variable));
    }

    public QDeliveryCode(Path<? extends DeliveryCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeliveryCode(PathMetadata metadata) {
        super(DeliveryCode.class, metadata);
    }

}

