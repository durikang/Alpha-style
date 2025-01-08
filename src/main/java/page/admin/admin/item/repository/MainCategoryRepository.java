package page.admin.admin.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.admin.item.domain.MainCategory;

import java.util.List;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {

    @Override
    @EntityGraph(attributePaths = "subCategories")
    List<MainCategory> findAll();
}

