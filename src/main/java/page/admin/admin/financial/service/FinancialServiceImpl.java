package page.admin.admin.financial.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
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

        // 필터 조건 구성
        BooleanBuilder predicate = new BooleanBuilder();

        // 키워드 필터 (제품명 기준)
        if (keyword != null && !keyword.isEmpty()) {
            predicate.and(qFinancialRecord.productName.containsIgnoreCase(keyword.trim()));
        }

        // 거래 유형 필터
        if (transactionType != null) {
            predicate.and(qFinancialRecord.transactionType.eq(transactionType));
        }

        // 시작 날짜 필터
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            predicate.and(
                    qFinancialRecord.year.goe(start.getYear())
                            .and(qFinancialRecord.month.goe(start.getMonthValue()))
                            .and(qFinancialRecord.day.goe(start.getDayOfMonth()))
            );
        }

        // 종료 날짜 필터
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate);
            predicate.and(
                    qFinancialRecord.year.loe(end.getYear())
                            .and(qFinancialRecord.month.loe(end.getMonthValue()))
                            .and(qFinancialRecord.day.loe(end.getDayOfMonth()))
            );
        }

        // 동적 정렬 조건 (OrderSpecifier 배열)
        OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(sortField, sortDirection, qFinancialRecord);

        // 데이터 조회 (페이징 적용)
        List<FinancialRecord> records = queryFactory
                .selectFrom(qFinancialRecord)
                .where(predicate)
                .orderBy(orderSpecifiers)
                .offset(page * size)
                .limit(size)
                .fetch();

        // DTO 변환
        List<FinancialRecordDTO> dtoList = records.stream()
                .map(this::toDto)
                .toList();

        // 전체 레코드 수 조회
        long totalRecords = queryFactory
                .selectFrom(qFinancialRecord)
                .where(predicate)
                .fetchCount();

        // 페이징 계산
        // - page: 0-based 인덱스로 전달되므로 currentPage는 page + 1
        // - 10페이지씩 끊어서 표시 (예: 1~10, 11~20, ...)
        int totalPages = (int) Math.ceil((double) totalRecords / size);
        int currentPage = page + 1;
        int blockSize = 10;
        int startPage = ((currentPage - 1) / blockSize) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPages);

        return new PagedFinancialRecordDTO(dtoList, currentPage, totalPages, startPage, endPage);
    }

    /**
     * 전달받은 정렬 필드와 방향에 따라 OrderSpecifier 배열을 생성한다.
     */
    private OrderSpecifier<?>[] getOrderSpecifiers(String sortField, String sortDirection, QFinancialRecord q) {
        boolean asc = sortDirection.equalsIgnoreCase("asc");
        if ("transactionDate".equalsIgnoreCase(sortField)) {
            // 거래일 정렬: 연도, 월, 일 순서
            return new OrderSpecifier<?>[] {
                    asc ? q.year.asc() : q.year.desc(),
                    asc ? q.month.asc() : q.month.desc(),
                    asc ? q.day.asc() : q.day.desc()
            };
        } else if ("transactionType".equalsIgnoreCase(sortField)) {
            return new OrderSpecifier<?>[] {
                    asc ? q.transactionType.asc() : q.transactionType.desc()
            };
        } else if ("productName".equalsIgnoreCase(sortField)) {
            return new OrderSpecifier<?>[] {
                    asc ? q.productName.asc() : q.productName.desc()
            };
        } else if ("quantity".equalsIgnoreCase(sortField)) {
            return new OrderSpecifier<?>[] {
                    asc ? q.quantity.asc() : q.quantity.desc()
            };
        } else if ("unitPrice".equalsIgnoreCase(sortField)) {
            return new OrderSpecifier<?>[] {
                    asc ? q.unitPrice.asc() : q.unitPrice.desc()
            };
        } else if ("supplyAmount".equalsIgnoreCase(sortField)) {
            return new OrderSpecifier<?>[] {
                    asc ? q.supplyAmount.asc() : q.supplyAmount.desc()
            };
        } else if ("vat".equalsIgnoreCase(sortField)) {
            return new OrderSpecifier<?>[] {
                    asc ? q.vat.asc() : q.vat.desc()
            };
        } else if ("totalAmount".equalsIgnoreCase(sortField)) {
            // 총 금액 = 공급 금액 + VAT
            NumberExpression<Double> totalAmount = q.supplyAmount.add(q.vat);
            return new OrderSpecifier<?>[] {
                    asc ? totalAmount.asc() : totalAmount.desc()
            };
        } else {
            // 기본: 거래일 정렬
            return new OrderSpecifier<?>[] {
                    asc ? q.year.asc() : q.year.desc(),
                    asc ? q.month.asc() : q.month.desc(),
                    asc ? q.day.asc() : q.day.desc()
            };
        }
    }

    /**
     * FinancialRecord 엔티티를 FinancialRecordDTO로 변환한다.
     */
    private FinancialRecordDTO toDto(FinancialRecord record) {
        // 거래 유형 문자열 변환 (1: 구매, 2: 판매)
        String transactionTypeStr = switch (record.getTransactionType()) {
            case 1 -> "구매";
            case 2 -> "판매";
            default -> "기타";
        };

        return new FinancialRecordDTO(
                String.format("%04d-%02d-%02d", record.getYear(), record.getMonth(), record.getDay()),
                record.getYear(),
                record.getMonth(),
                record.getDay(),
                transactionTypeStr,
                record.getProductName(),
                record.getQuantity(),
                record.getUnitPrice(),
                record.getSupplyAmount(),
                record.getVat(),
                record.getSupplyAmount() + record.getVat()
        );
    }
}
