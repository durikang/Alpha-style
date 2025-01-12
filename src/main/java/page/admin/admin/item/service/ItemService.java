package page.admin.admin.item.service;

import page.admin.admin.item.domain.Review;
import page.admin.admin.item.domain.dto.*;
import page.admin.admin.manager.domain.dto.CategoryWithItemsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.item.domain.Item;
import page.admin.user.member.domain.dto.LoginSessionInfo;

import java.util.List;
import java.util.Set;

public interface ItemService {
    Page<Item> searchItems(String keyword, Pageable pageable);

    // 등록
    Item saveItem(ItemSaveForm form, LoginSessionInfo seller);

    // 단일 엔티티 반환
    Item getItem(Long id);

    // 상세 DTO
    ItemViewForm getItemViewForm(Long id);

    // 수정 폼 DTO
    ItemEditForm getItemEditForm(Long id);

    // 수정
    void updateItem(Long id, ItemUpdateForm form);

    // 전체 조회
    List<Item> getAllItems();

    // 단일 삭제
    void deleteItem(Long id);

    // 다중 삭제
    void deleteItems(List<Long> itemIds);

    // 지역 코드 조회
    Set<String> getItemRegions(Long itemId);

    /**
     * 메인 카테고리별로 최대 4개의 상품을 조회하는 메서드
     */
    List<CategoryWithItemsDTO> getItemsGroupedByMainCategory(int limitPerCategory);

    List<Item> findItemsByMainCategoryWithOffset(Long mainCategoryId, int offset, int limit);

    // 인기상품 조회
    List<Item> getPopularItems(Pageable pageable);

    // 조회수 증가
    void incrementViewCount(Long itemId);

    // 평균 평점 계산
    Double getAverageRating(Long itemId);


}
