package page.admin.admin.item.service;

// package page.admin.admin.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.item.domain.Review;

public interface ReviewService {
    Page<Review> getReviewsByItemId(Long itemId, Pageable pageable);
    Review createReview(Long itemId, Long memberId, Integer rating, String comment);
    void deleteReview(Long reviewId);
    Review getReviewById(Long reviewId);
}
