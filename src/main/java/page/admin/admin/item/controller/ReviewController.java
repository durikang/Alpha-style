package page.admin.admin.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.admin.item.domain.Review;
import page.admin.admin.item.domain.dto.ReviewDTO;
import page.admin.admin.item.service.ReviewService;
import page.admin.common.utils.Alert;
import page.admin.user.member.domain.dto.LoginSessionInfo;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin/product/items/{itemId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 목록 조회 (비동기)
     */
    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getReviews(@PathVariable("itemId") Long itemId,
                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviewPage = reviewService.getReviewsByItemId(itemId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewPage.getContent());
        response.put("currentPage", reviewPage.getNumber());
        response.put("totalPages", reviewPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * 리뷰 작성 처리
     */
    @PostMapping
    public String addReview(@PathVariable("itemId") Long itemId,
                            @Valid @ModelAttribute("review") ReviewDTO reviewDTO,
                            BindingResult bindingResult,
                            @SessionAttribute(name = "loginMember", required = false) LoginSessionInfo loginSessionInfo,
                            RedirectAttributes redirectAttributes) {
        if (loginSessionInfo == null) {
            redirectAttributes.addFlashAttribute("alert", new Alert("로그인이 필요합니다.", Alert.AlertType.WARNING));
            return "redirect:/admin/product/items/" + itemId;
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("alert", new Alert("리뷰 작성에 실패했습니다.", Alert.AlertType.ERROR));
            return "redirect:/admin/product/items/" + itemId;
        }

        try {
            reviewService.createReview(itemId, loginSessionInfo.getUserNo(), reviewDTO.getRating(), reviewDTO.getComment());
            redirectAttributes.addFlashAttribute("alert", new Alert("리뷰가 성공적으로 작성되었습니다.", Alert.AlertType.SUCCESS));
        } catch (Exception e) {
            log.error("리뷰 작성 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("alert", new Alert("리뷰 작성 중 오류가 발생했습니다.", Alert.AlertType.ERROR));
        }

        return "redirect:/admin/product/items/" + itemId;
    }

    /**
     * 리뷰 삭제 처리
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteReview(@PathVariable("itemId") Long itemId,
                                          @RequestParam("reviewId") Long reviewId,
                                          @SessionAttribute(name = "loginMember", required = false) LoginSessionInfo loginSessionInfo) {
        if (loginSessionInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            Review review = reviewService.getReviewById(reviewId);
            if (!review.getMember().getUserNo().equals(loginSessionInfo.getUserNo()) &&
                    !"ADMIN".equalsIgnoreCase(loginSessionInfo.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "권한이 없습니다."));
            }

            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok(Map.of("message", "리뷰가 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            log.error("리뷰 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "리뷰 삭제 중 오류가 발생했습니다."));
        }
    }
}
