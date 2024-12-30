package page.admin.admin.item.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class MainCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "main_category_seq_generator")
    @SequenceGenerator(name = "main_category_seq_generator", sequenceName = "main_category_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "메인 카테고리 이름은 필수 입력 사항입니다.")
    private String mainCategoryName;

    @OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubCategory> subCategories;
}
