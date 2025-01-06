package page.admin.admin.manager.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSlider is a Querydsl query type for Slider
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSlider extends EntityPathBase<Slider> {

    private static final long serialVersionUID = -1699080755L;

    public static final QSlider slider = new QSlider("slider");

    public final BooleanPath active = createBoolean("active");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final NumberPath<Integer> orderIndex = createNumber("orderIndex", Integer.class);

    public QSlider(String variable) {
        super(Slider.class, forVariable(variable));
    }

    public QSlider(Path<? extends Slider> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSlider(PathMetadata metadata) {
        super(Slider.class, metadata);
    }

}

