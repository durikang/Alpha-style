package page.admin.admin.item.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_category_seq_generator")
    @SequenceGenerator(name = "sub_category_seq_generator", sequenceName = "sub_category_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "서브 카테고리 이름은 필수 입력 사항입니다.")
    @Column(nullable = false)
    private String subCategoryName;

    @NotNull(message = "메인 카테고리는 필수 입력 사항입니다.")
    @ManyToOne
    @JoinColumn(name = "main_category_id", nullable = false)
    private MainCategory mainCategory;
}
