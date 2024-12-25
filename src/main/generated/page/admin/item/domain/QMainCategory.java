package page.admin.item.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMainCategory is a Querydsl query type for MainCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMainCategory extends EntityPathBase<MainCategory> {

    private static final long serialVersionUID = 474382468L;

    public static final QMainCategory mainCategory = new QMainCategory("mainCategory");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath mainCategoryName = createString("mainCategoryName");

    public final ListPath<SubCategory, QSubCategory> subCategories = this.<SubCategory, QSubCategory>createList("subCategories", SubCategory.class, QSubCategory.class, PathInits.DIRECT2);

    public QMainCategory(String variable) {
        super(MainCategory.class, forVariable(variable));
    }

    public QMainCategory(Path<? extends MainCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMainCategory(PathMetadata metadata) {
        super(MainCategory.class, metadata);
    }

}

