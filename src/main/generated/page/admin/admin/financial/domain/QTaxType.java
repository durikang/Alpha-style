package page.admin.admin.financial.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTaxType is a Querydsl query type for TaxType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTaxType extends EntityPathBase<TaxType> {

    private static final long serialVersionUID = 1597663869L;

    public static final QTaxType taxType1 = new QTaxType("taxType1");

    public final StringPath description = createString("description");

    public final NumberPath<Long> taxCode = createNumber("taxCode", Long.class);

    public final StringPath taxType = createString("taxType");

    public QTaxType(String variable) {
        super(TaxType.class, forVariable(variable));
    }

    public QTaxType(Path<? extends TaxType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTaxType(PathMetadata metadata) {
        super(TaxType.class, metadata);
    }

}

