package page.admin.admin.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.admin.item.domain.Item;
import page.admin.admin.item.domain.Review;
import page.admin.admin.item.domain.dto.ReviewDTO;
import page.admin.admin.item.repository.ItemRepository;
import page.admin.admin.item.repository.ReviewRepository;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.repository.MemberRepository;
import page.admin.common.utils.exception.DataNotFoundException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByItemId(Long itemId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByItemItemId(itemId, pageable);

        // 리뷰가 없으면 빈 페이지 반환
        if (reviewPage == null || reviewPage.isEmpty()) {
            return Page.empty();
        }

        return reviewPage.map(review -> new ReviewDTO(
                review.getReviewId(),
                review.getMember().getUsername(),
                review.getRating(),
                review.getReviewComment(),
                review.getCreatedDate()
        ));
    }

    @Override
    public Review createReview(Long itemId, Long memberId, Double rating, String comment) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("해당 아이템을 찾을 수 없습니다. ID: " + itemId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new DataNotFoundException("해당 회원을 찾을 수 없습니다. ID: " + memberId));

        Review review = new Review();
        review.setItem(item);
        review.setMember(member);
        review.setRating(rating);
        review.setReviewComment(comment);

        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new DataNotFoundException("해당 리뷰를 찾을 수 없습니다. ID: " + reviewId));
        reviewRepository.delete(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new DataNotFoundException("해당 리뷰를 찾을 수 없습니다. ID: " + reviewId));
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long itemId) {
        Double averageRating = reviewRepository.findAverageRatingByItemId(itemId);
        return averageRating != null ? averageRating : 0.0;
    }

}
