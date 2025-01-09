package page.admin.admin.item.repository.custom;

import page.admin.admin.item.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    /**
     * 키워드를 기반으로 상품을 검색하여 페이지 단위로 반환합니다.
     *
     * @param keyword  검색할 키워드
     * @param pageable 페이지 요청 정보
     * @return 검색된 상품의 페이지
     */
    Page<Item> searchItems(String keyword, Pageable pageable);
}
