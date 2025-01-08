package page.admin.admin.item.service;

// package page.admin.admin.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.admin.item.domain.Item;
import page.admin.admin.item.domain.Review;
import page.admin.admin.item.repository.ItemRepository;
import page.admin.admin.item.repository.ReviewRepository;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.repository.MemberRepository;
import page.admin.common.utils.exception.DataNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Review> getReviewsByItemId(Long itemId, Pageable pageable) {
        return reviewRepository.findByItemItemId(itemId, pageable);
    }

    @Override
    public Review createReview(Long itemId, Long memberId, Integer rating, String comment) {
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
}
