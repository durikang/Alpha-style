package page.admin.admin.manager.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileSettings is a Querydsl query type for FileSettings
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFileSettings extends EntityPathBase<FileSettings> {

    private static final long serialVersionUID = -2082400853L;

    public static final QFileSettings fileSettings = new QFileSettings("fileSettings");

    public final StringPath allowedExtensions = createString("allowedExtensions");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> maxFileSize = createNumber("maxFileSize", Long.class);

    public QFileSettings(String variable) {
        super(FileSettings.class, forVariable(variable));
    }

    public QFileSettings(Path<? extends FileSettings> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileSettings(PathMetadata metadata) {
        super(FileSettings.class, metadata);
    }

}

