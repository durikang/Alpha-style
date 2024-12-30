package page.admin.admin.item.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class ItemType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_type_seq_generator")
    @SequenceGenerator(name = "item_type_seq_generator", sequenceName = "item_type_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "상품 종류 코드는 필수 입력 사항입니다.")
    private String code;

    @NotBlank(message = "설명은 필수 입력 사항입니다.")
    private String description;
}
