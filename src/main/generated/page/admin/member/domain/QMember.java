package page.admin.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1522557646L;

    public static final QMember member = new QMember("member1");

    public final page.admin.common.QBaseEntity _super = new page.admin.common.QBaseEntity(this);

    public final StringPath address = createString("address");

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final ListPath<page.admin.item.domain.Item, page.admin.item.domain.QItem> items = this.<page.admin.item.domain.Item, page.admin.item.domain.QItem>createList("items", page.admin.item.domain.Item.class, page.admin.item.domain.QItem.class, PathInits.DIRECT2);

    public final StringPath mobilePhone = createString("mobilePhone");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath password = createString("password");

    public final StringPath role = createString("role");

    public final StringPath secondaryAddress = createString("secondaryAddress");

    public final StringPath securityAnswer = createString("securityAnswer");

    public final StringPath securityQuestion = createString("securityQuestion");

    public final StringPath userId = createString("userId");

    public final StringPath username = createString("username");

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public final StringPath zipCode = createString("zipCode");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

