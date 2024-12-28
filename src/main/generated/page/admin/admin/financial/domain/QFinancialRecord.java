package page.admin.admin.financial.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFinancialRecord is a Querydsl query type for FinancialRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFinancialRecord extends EntityPathBase<FinancialRecord> {

    private static final long serialVersionUID = 991029042L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFinancialRecord financialRecord = new QFinancialRecord("financialRecord");

    public final NumberPath<Integer> day = createNumber("day", Integer.class);

    public final page.admin.admin.item.domain.QItem item;

    public final NumberPath<Integer> month = createNumber("month", Integer.class);

    public final StringPath productName = createString("productName");

    public final NumberPath<Double> quantity = createNumber("quantity", Double.class);

    public final NumberPath<Long> recordNo = createNumber("recordNo", Long.class);

    public final NumberPath<Double> supplyAmount = createNumber("supplyAmount", Double.class);

    public final QTaxType taxType;

    public final NumberPath<Integer> transactionType = createNumber("transactionType", Integer.class);

    public final NumberPath<Double> unitPrice = createNumber("unitPrice", Double.class);

    public final page.admin.admin.member.domain.QMember user;

    public final NumberPath<Double> vat = createNumber("vat", Double.class);

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QFinancialRecord(String variable) {
        this(FinancialRecord.class, forVariable(variable), INITS);
    }

    public QFinancialRecord(Path<? extends FinancialRecord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFinancialRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFinancialRecord(PathMetadata metadata, PathInits inits) {
        this(FinancialRecord.class, metadata, inits);
    }

    public QFinancialRecord(Class<? extends FinancialRecord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new page.admin.admin.item.domain.QItem(forProperty("item"), inits.get("item")) : null;
        this.taxType = inits.isInitialized("taxType") ? new QTaxType(forProperty("taxType")) : null;
        this.user = inits.isInitialized("user") ? new page.admin.admin.member.domain.QMember(forProperty("user")) : null;
    }

}

