package page.admin.admin.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import page.admin.admin.item.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"member"})
    Page<Review> findByItemItemId(Long itemId, Pageable pageable);

    void deleteByItemItemId(Long itemId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.item.itemId = :itemId")
    Double findAverageRatingByItemId(@Param("itemId") Long itemId);
}
