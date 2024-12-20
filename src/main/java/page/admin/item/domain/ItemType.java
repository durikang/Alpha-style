package page.admin.item.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ItemType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_type_seq_generator")
    @SequenceGenerator(name = "item_type_seq_generator", sequenceName = "item_type_seq", allocationSize = 1)
    private Long id;

    private String code;
    private String description;
}
