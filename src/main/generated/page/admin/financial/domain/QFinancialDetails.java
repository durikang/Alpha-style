package page.admin.financial.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFinancialDetails is a Querydsl query type for FinancialDetails
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFinancialDetails extends EntityPathBase<FinancialDetails> {

    private static final long serialVersionUID = -1784987680L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFinancialDetails financialDetails = new QFinancialDetails("financialDetails");

    public final NumberPath<Double> costOfGoodsSold = createNumber("costOfGoodsSold", Double.class);

    public final NumberPath<Long> detailNo = createNumber("detailNo", Long.class);

    public final QFinancialRecord financialRecord;

    public final NumberPath<Double> netIncome = createNumber("netIncome", Double.class);

    public final NumberPath<Double> salesRevenue = createNumber("salesRevenue", Double.class);

    public final NumberPath<Double> sgAndAExpenses = createNumber("sgAndAExpenses", Double.class);

    public QFinancialDetails(String variable) {
        this(FinancialDetails.class, forVariable(variable), INITS);
    }

    public QFinancialDetails(Path<? extends FinancialDetails> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFinancialDetails(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFinancialDetails(PathMetadata metadata, PathInits inits) {
        this(FinancialDetails.class, metadata, inits);
    }

    public QFinancialDetails(Class<? extends FinancialDetails> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.financialRecord = inits.isInitialized("financialRecord") ? new QFinancialRecord(forProperty("financialRecord"), inits.get("financialRecord")) : null;
    }

}

