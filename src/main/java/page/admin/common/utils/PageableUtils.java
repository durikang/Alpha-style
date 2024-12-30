package page.admin.common.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

public class PageableUtils {

    /**
     * Pageable 객체 생성
     *
     * @param page    현재 페이지 (0-based)
     * @param size    페이지 크기
     * @param sortBy  정렬 기준 필드
     * @param sortDir 정렬 방향 (asc/desc)
     * @return Pageable 객체
     */
    public static Pageable createPageRequest(int page, int size, String sortBy, String sortDir) {
        Sort sort;
        try {
            sort = Sort.by(Sort.Direction.fromString(sortDir.toUpperCase()), sortBy);
        } catch (IllegalArgumentException e) {
            sort = Sort.by(Sort.Direction.ASC, sortBy); // 기본값: 오름차순
        }
        return PageRequest.of(page, size, sort);
    }

    /**
     * 페이지네이션 속성 추가
     *
     * @param model     Thymeleaf 모델
     * @param pageData  페이징 데이터
     * @param keyword   검색 키워드
     * @param sortBy    정렬 기준 필드
     * @param sortDir   정렬 방향 (asc/desc)
     * @param blockSize 블록 크기 (기본: 10)
     */
    public static void addPaginationAttributes(Model model, Page<?> pageData, String keyword, String sortBy, String sortDir, int blockSize) {
        int currentPage = pageData.getNumber() + 1; // 0-based → 1-based
        int totalPages = pageData.getTotalPages();

        // 블록 단위 페이지네이션 계산
        int currentBlock = (currentPage - 1) / blockSize;
        int startPage = currentBlock * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPages);

        // Thymeleaf 모델에 데이터 추가
        model.addAttribute("data", pageData.getContent());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortBy);
        model.addAttribute("sortDirection", sortDir);
    }

    /**
     * 기본 블록 크기 10으로 페이징 속성 추가
     */
    public static void addPaginationAttributes(Model model, Page<?> pageData, String keyword, String sortBy, String sortDir) {
        addPaginationAttributes(model, pageData, keyword, sortBy, sortDir, 10);
    }
}
