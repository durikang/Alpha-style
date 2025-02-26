package page.admin.admin.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import page.admin.admin.item.domain.Item;
import page.admin.admin.item.repository.custom.ItemRepositoryCustom;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    @EntityGraph(attributePaths = {"mainCategory", "subCategory", "seller", "reviews"})
    Page<Item> findByItemNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.thumbnails WHERE i.itemId = :id")
    Optional<Item> findByIdWithThumbnails(@Param("id") Long id);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.regions WHERE i.itemId = :id")
    Optional<Item> findByIdWithRegions(@Param("id") Long id);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.thumbnails " +
            "LEFT JOIN FETCH i.regions " +
            "LEFT JOIN FETCH i.mainCategory " +
            "LEFT JOIN FETCH i.subCategory " +
            "LEFT JOIN FETCH i.reviews " + // 리뷰 추가
            "WHERE i.itemId = :id")
    Optional<Item> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT i FROM Item i WHERE i.mainCategory.id = :mainCategoryId")
    List<Item> findByMainCategoryId(@Param("mainCategoryId") Long mainCategoryId);

    @Query("SELECT i FROM Item i WHERE i.subCategory.id = :subCategoryId")
    List<Item> findBySubCategoryId(@Param("subCategoryId") Long subCategoryId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.mainCategory.id = :mainCategoryId " +
            "AND LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Item> searchByMainCategory(@Param("mainCategoryId") Long mainCategoryId,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "WHERE i.subCategory.id = :subCategoryId " +
            "AND LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Item> searchBySubCategory(@Param("subCategoryId") Long subCategoryId,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);

    /**
     * 메인 카테고리별로 최대 4개의 상품을 조회하는 메서드
     */
    @Query("SELECT i FROM Item i WHERE i.mainCategory.id = :mainCategoryId AND i.open = true ORDER BY i.salePrice DESC")
    List<Item> findTop4ByMainCategoryIdAndOpenTrueOrderBySalePriceDesc(@Param("mainCategoryId") Long mainCategoryId);

    /**
     * 메인 카테고리별로 페이징된 상품을 조회하는 메서드
     */
    Page<Item> findByMainCategoryIdAndOpenTrue(Long mainCategoryId, Pageable pageable);

    /**
     * 인기상품 조회: 조회수, 판매량, 평점 등을 기준으로 정렬
     * 예시: 조회수 DESC, 판매량 DESC, 평균 평점 DESC
     */
    @Query("SELECT i FROM Item i LEFT JOIN i.reviews r GROUP BY i ORDER BY SUM(i.viewCount) DESC, SUM(i.salesCount) DESC, AVG(r.rating) DESC")
    List<Item> findPopularItems(Pageable pageable);
}
