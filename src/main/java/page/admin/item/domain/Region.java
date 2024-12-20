package page.admin.item.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "region_seq_generator")
    @SequenceGenerator(name = "region_seq_generator", sequenceName = "region_seq", allocationSize = 1)
    private Long id;

    private String code;
    private String displayName;
    private Boolean active;
}
