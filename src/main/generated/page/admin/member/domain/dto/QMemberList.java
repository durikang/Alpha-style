package page.admin.member.domain.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * page.admin.member.domain.dto.QMemberList is a Querydsl Projection type for MemberList
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMemberList extends ConstructorExpression<MemberList> {

    private static final long serialVersionUID = -247214597L;

    public QMemberList(com.querydsl.core.types.Expression<Long> userNo, com.querydsl.core.types.Expression<String> userId, com.querydsl.core.types.Expression<String> username, com.querydsl.core.types.Expression<String> email, com.querydsl.core.types.Expression<String> mobilePhone, com.querydsl.core.types.Expression<String> role) {
        super(MemberList.class, new Class<?>[]{long.class, String.class, String.class, String.class, String.class, String.class}, userNo, userId, username, email, mobilePhone, role);
    }

}

