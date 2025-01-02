package page.admin.user.member.domain.social;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNaverUser is a Querydsl query type for NaverUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNaverUser extends EntityPathBase<NaverUser> {

    private static final long serialVersionUID = -197899L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNaverUser naverUser = new QNaverUser("naverUser");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final page.admin.admin.member.domain.QMember member;

    public final StringPath mobilePhone = createString("mobilePhone");

    public final StringPath providerId = createString("providerId");

    public final StringPath username = createString("username");

    public QNaverUser(String variable) {
        this(NaverUser.class, forVariable(variable), INITS);
    }

    public QNaverUser(Path<? extends NaverUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNaverUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNaverUser(PathMetadata metadata, PathInits inits) {
        this(NaverUser.class, metadata, inits);
    }

    public QNaverUser(Class<? extends NaverUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new page.admin.admin.member.domain.QMember(forProperty("member")) : null;
    }

}

