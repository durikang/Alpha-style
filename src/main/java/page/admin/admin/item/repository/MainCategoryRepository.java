package page.admin.admin.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import page.admin.admin.item.domain.MainCategory;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
    MainCategory findByMainCategoryName(String mainCategoryName);
}

