package page.admin.admin.financial.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import page.admin.admin.financial.domain.FinancialRecord;
import page.admin.admin.financial.domain.QFinancialRecord;
import page.admin.admin.financial.domain.dto.FinancialRecordDTO;
import page.admin.admin.financial.domain.dto.PagedFinancialRecordDTO;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialServiceImpl implements FinancialService {

    private final JPAQueryFactory queryFactory;


    @Override
    public PagedFinancialRecordDTO getFilteredRecords(String keyword, Integer transactionType, String startDate, String endDate,
                                                      String sortField, String sortDirection, int page, int size) {
        QFinancialRecord qFinancialRecord = QFinancialRecord.financialRecord;

        BooleanBuilder predicate = new BooleanBuilder();

        // 키워드 필터
        if (keyword != null && !keyword.isEmpty()) {
            predicate.and(qFinancialRecord.productName.containsIgnoreCase(keyword.trim()));
        }

        // 거래 유형 필터
        if (transactionType != null) {
            predicate.and(qFinancialRecord.transactionType.eq(transactionType));
        }

        // 날짜 필터
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            predicate.and(
                    qFinancialRecord.year.goe(start.getYear())
                            .and(qFinancialRecord.month.goe(start.getMonthValue()))
                            .and(qFinancialRecord.day.goe(start.getDayOfMonth()))
            );
        }

        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate);
            predicate.and(
                    qFinancialRecord.year.loe(end.getYear())
                            .and(qFinancialRecord.month.loe(end.getMonthValue()))
                            .and(qFinancialRecord.day.loe(end.getDayOfMonth()))
            );
        }

        // 정렬 조건
        OrderSpecifier<?> yearOrder = sortDirection.equalsIgnoreCase("asc")
                ? qFinancialRecord.year.asc()
                : qFinancialRecord.year.desc();
        OrderSpecifier<?> monthOrder = sortDirection.equalsIgnoreCase("asc")
                ? qFinancialRecord.month.asc()
                : qFinancialRecord.month.desc();
        OrderSpecifier<?> dayOrder = sortDirection.equalsIgnoreCase("asc")
                ? qFinancialRecord.day.asc()
                : qFinancialRecord.day.desc();

        // 데이터 조회
        List<FinancialRecord> records = queryFactory
                .selectFrom(qFinancialRecord)
                .where(predicate)
                .orderBy(yearOrder, monthOrder, dayOrder)
                .offset(page * size)
                .limit(size)
                .fetch();

        // DTO 변환
        List<FinancialRecordDTO> dtoList = records.stream()
                .map(this::toDto)
                .toList();

        // 전체 데이터 개수
        long totalRecords = queryFactory
                .selectFrom(qFinancialRecord)
                .where(predicate)
                .fetchCount();

        int totalPages = (int) Math.ceil((double) totalRecords / size);
        int currentPage = page + 1;
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        return new PagedFinancialRecordDTO(dtoList, currentPage, totalPages, startPage, endPage);
    }



    private FinancialRecordDTO toDto(FinancialRecord record) {
        // 거래 유형 문자열 처리
        String transactionType = switch (record.getTransactionType()) {
            case 1 -> "구매";
            case 2 -> "판매";
            default -> "기타";
        };

        // DTO 생성 및 반환
        return new FinancialRecordDTO(
                String.format("%04d-%02d-%02d", record.getYear(), record.getMonth(), record.getDay()),
                record.getYear(),
                record.getMonth(),
                record.getDay(),
                transactionType,
                record.getProductName(),
                record.getQuantity(),
                record.getUnitPrice(),
                record.getSupplyAmount(),
                record.getVat(),
                record.getSupplyAmount() + record.getVat()
        );
    }



}
