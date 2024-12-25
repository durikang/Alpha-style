package page.admin.item.domain;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String subCategoryName; // Getter 필요

    @ManyToOne
    @JoinColumn(name = "main_category_id", nullable = false)
    private MainCategory mainCategory; // 소속 메인 카테고리
}

