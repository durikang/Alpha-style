package page.admin.item.domain;

import jakarta.persistence.*;
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

    private String mainCategoryName; // Getter 필요

    @OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubCategory> subCategories;
}
