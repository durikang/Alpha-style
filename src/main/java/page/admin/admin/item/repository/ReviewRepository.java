package page.admin.admin.item.repository;

// package page.admin.admin.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.admin.item.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByItemItemId(Long itemId, Pageable pageable);
    void deleteByItemItemId(Long itemId);
}
