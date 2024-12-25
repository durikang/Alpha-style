package page.admin.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import page.admin.item.domain.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> { // ID 타입은 Long입니다.

    // 키워드 검색
    @EntityGraph(attributePaths = {"mainCategory", "subCategory", "seller"})
    Page<Item> findByItemNameContainingIgnoreCase(String keyword, Pageable pageable);

    // 특정 ID에 대해 썸네일만 즉시 로딩
    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.thumbnails WHERE i.itemId = :id")
    Optional<Item> findByIdWithThumbnails(@Param("id") Long id);

    // 특정 ID에 대해 지역만 즉시 로딩
    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.regions WHERE i.itemId = :id")
    Optional<Item> findByIdWithRegions(@Param("id") Long id);

    // 특정 ID에 대해 모든 관계 즉시 로딩
    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.thumbnails " +
            "LEFT JOIN FETCH i.regions " +
            "LEFT JOIN FETCH i.mainCategory " +
            "LEFT JOIN FETCH i.subCategory " +
            "WHERE i.itemId = :id")
    Optional<Item> findByIdWithDetails(@Param("id") Long id);

    // 특정 메인 카테고리에 속한 아이템 조회
    @Query("SELECT i FROM Item i WHERE i.mainCategory.id = :mainCategoryId")
    List<Item> findByMainCategoryId(@Param("mainCategoryId") Long mainCategoryId);

    // 특정 서브 카테고리에 속한 아이템 조회
    @Query("SELECT i FROM Item i WHERE i.subCategory.id = :subCategoryId")
    List<Item> findBySubCategoryId(@Param("subCategoryId") Long subCategoryId);

    // 특정 메인 카테고리와 키워드로 검색
    @Query("SELECT i FROM Item i " +
            "WHERE i.mainCategory.id = :mainCategoryId " +
            "AND LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Item> searchByMainCategory(@Param("mainCategoryId") Long mainCategoryId,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);

    // 특정 서브 카테고리와 키워드로 검색
    @Query("SELECT i FROM Item i " +
            "WHERE i.subCategory.id = :subCategoryId " +
            "AND LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Item> searchBySubCategory(@Param("subCategoryId") Long subCategoryId,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);


}
