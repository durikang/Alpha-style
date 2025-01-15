package page.admin.admin.manager.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLogo is a Querydsl query type for Logo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLogo extends EntityPathBase<Logo> {

    private static final long serialVersionUID = 1843834263L;

    public static final QLogo logo = new QLogo("logo");

    public final BooleanPath active = createBoolean("active");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath logoName = createString("logoName");

    public final StringPath originalFileName = createString("originalFileName");

    public final StringPath storedFileName = createString("storedFileName");

    public QLogo(String variable) {
        super(Logo.class, forVariable(variable));
    }

    public QLogo(Path<? extends Logo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLogo(PathMetadata metadata) {
        super(Logo.class, metadata);
    }

}

