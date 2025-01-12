package page.admin.admin.item.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "region_seq_generator")
    @SequenceGenerator(name = "region_seq_generator", sequenceName = "region_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "코드는 필수 입력 사항입니다.")
    private String code;

    @NotBlank(message = "지역 이름은 필수 입력 사항입니다.")
    private String displayName;

    @NotNull(message = "활성 상태는 필수 입력 사항입니다.")
    private Boolean active;
}
