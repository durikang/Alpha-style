package page.admin.admin.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import page.admin.admin.item.domain.MainCategory;
import page.admin.admin.item.domain.SubCategory;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    // 필요한 경우 커스텀 메서드 선언 가능
    SubCategory findBySubCategoryName(String subCategoryName);

    List<SubCategory> findAllByMainCategory(MainCategory mainCategory);
}
