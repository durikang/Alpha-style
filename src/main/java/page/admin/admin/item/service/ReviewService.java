package page.admin.admin.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.item.domain.Review;
import page.admin.admin.item.domain.dto.ReviewDTO;

public interface ReviewService {
    Page<ReviewDTO> getReviewsByItemId(Long itemId, Pageable pageable);
    Review createReview(Long itemId, Long memberId, Integer rating, String comment);
    void deleteReview(Long reviewId);
    Review getReviewById(Long reviewId);
    Double getAverageRating(Long itemId); // 추가된 메서드
}
