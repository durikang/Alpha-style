package page.admin.common.log.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSystemLog is a Querydsl query type for SystemLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSystemLog extends EntityPathBase<SystemLog> {

    private static final long serialVersionUID = 347415962L;

    public static final QSystemLog systemLog = new QSystemLog("systemLog");

    public final StringPath actionType = createString("actionType");

    public final StringPath details = createString("details");

    public final NumberPath<Long> entityId = createNumber("entityId", Long.class);

    public final StringPath entityName = createString("entityName");

    public final NumberPath<Long> logId = createNumber("logId", Long.class);

    public final StringPath performedBy = createString("performedBy");

    public final DateTimePath<java.time.LocalDateTime> performedDate = createDateTime("performedDate", java.time.LocalDateTime.class);

    public QSystemLog(String variable) {
        super(SystemLog.class, forVariable(variable));
    }

    public QSystemLog(Path<? extends SystemLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSystemLog(PathMetadata metadata) {
        super(SystemLog.class, metadata);
    }

}

