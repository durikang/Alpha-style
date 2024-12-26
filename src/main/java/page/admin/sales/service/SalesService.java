package page.admin.sales.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.sales.domain.dto.SalesRecordDto;

import java.time.LocalDate;

public interface SalesService {

    /**
     * 판매 기록 조회
     *
     * @param pageable   페이징 및 정렬 정보
     * @param keyword    검색어 (상품명 또는 구매자 이름)
     * @param startDate  조회 시작 날짜
     * @param endDate    조회 종료 날짜
     * @return 판매 기록 페이지
     */
    Page<SalesRecordDto> getSalesRecords(Pageable pageable, String keyword, LocalDate startDate, LocalDate endDate);

}
