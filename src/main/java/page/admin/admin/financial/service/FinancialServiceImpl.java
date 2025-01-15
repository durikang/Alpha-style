package page.admin.admin.financial.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import page.admin.admin.financial.domain.FinancialRecord;
import page.admin.admin.financial.domain.QFinancialRecord;
import page.admin.admin.financial.domain.dto.FinancialRecordDTO;
import page.admin.admin.financial.domain.dto.PagedFinancialRecordDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialServiceImpl implements FinancialService {

    private final JPAQueryFactory queryFactory;

    private OrderSpecifier<?> getOrderSpecifier(String sortField, String sortDirection, QFinancialRecord qFinancialRecord) {
        Expression<String> expression; // Expression<String>으로 타입 명시
        switch (sortField) {
            case "transactionType":
                expression = qFinancialRecord.transactionType.stringValue(); // 정렬 기준이 문자열
                break;
            case "productName":
                expression = qFinancialRecord.productName;
                break;
            case "totalAmount":
                // Add는 NumberExpression을 반환하므로 String으로 변환 필요
                expression = qFinancialRecord.supplyAmount.add(qFinancialRecord.vat).stringValue();
                break;
            default: // 기본 정렬은 날짜 기준
                expression = Expressions.stringTemplate(
                        "CONCAT({0}, '-', LPAD({1}, 2, '0'), '-', LPAD({2}, 2, '0'))",
                        qFinancialRecord.year,
                        qFinancialRecord.month,
                        qFinancialRecord.day
                );
        }

        return sortDirection.equalsIgnoreCase("asc")
                ? new OrderSpecifier<>(Order.ASC, expression)
                : new OrderSpecifier<>(Order.DESC, expression);
    }



    @Override
    public PagedFinancialRecordDTO getFilteredRecords(String keyword, Integer transactionType, String startDate, String endDate,
                                                      String sortField, String sortDirection, int page, int size) {
        QFinancialRecord qFinancialRecord = QFinancialRecord.financialRecord;

        BooleanBuilder predicate = new BooleanBuilder();
        if (keyword != null && !keyword.isEmpty()) {
            predicate.and(qFinancialRecord.productName.containsIgnoreCase(keyword));
        }
        if (transactionType != null) {
            predicate.and(qFinancialRecord.transactionType.eq(transactionType));
        }
        if (startDate != null && !startDate.isEmpty()) {
            predicate.and(qFinancialRecord.year.goe(Integer.parseInt(startDate.split("-")[0]))
                    .and(qFinancialRecord.month.goe(Integer.parseInt(startDate.split("-")[1])))
                    .and(qFinancialRecord.day.goe(Integer.parseInt(startDate.split("-")[2]))));
        }
        if (endDate != null && !endDate.isEmpty()) {
            predicate.and(qFinancialRecord.year.loe(Integer.parseInt(endDate.split("-")[0]))
                    .and(qFinancialRecord.month.loe(Integer.parseInt(endDate.split("-")[1])))
                    .and(qFinancialRecord.day.loe(Integer.parseInt(endDate.split("-")[2]))));
        }

        // 날짜를 문자열로 결합
        StringExpression transactionDateExpression = Expressions.stringTemplate(
                "CONCAT({0}, '-', LPAD({1}, 2, '0'), '-', LPAD({2}, 2, '0'))",
                qFinancialRecord.year,
                qFinancialRecord.month,
                qFinancialRecord.day
        );

        // 정렬 조건 생성
        OrderSpecifier<?> order = sortDirection.equalsIgnoreCase("asc")
                ? transactionDateExpression.asc()
                : transactionDateExpression.desc();

        // 데이터 조회
        List<FinancialRecord> records = queryFactory
                .selectFrom(qFinancialRecord)
                .where(predicate)
                .orderBy(order)
                .offset(page * size)
                .limit(size)
                .fetch();

        List<FinancialRecordDTO> dtoList = records.stream()
                .map(this::toDto)
                .toList();

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


    @Override
    public byte[] generateExcel(String keyword, Integer transactionType, String startDate, String endDate,
                                String sortField, String sortDirection) {
        QFinancialRecord qFinancialRecord = QFinancialRecord.financialRecord;

        BooleanBuilder predicate = new BooleanBuilder();
        if (keyword != null && !keyword.isEmpty()) {
            predicate.and(qFinancialRecord.productName.containsIgnoreCase(keyword));
        }
        if (transactionType != null) {
            predicate.and(qFinancialRecord.transactionType.eq(transactionType));
        }

        // Start Date 조건 추가
        if (startDate != null && !startDate.isEmpty()) {
            String[] startParts = startDate.split("-");
            int startYear = Integer.parseInt(startParts[0]);
            int startMonth = Integer.parseInt(startParts[1]);
            int startDay = Integer.parseInt(startParts[2]);

            predicate.and(qFinancialRecord.year.gt(startYear)
                    .or(qFinancialRecord.year.eq(startYear).and(qFinancialRecord.month.gt(startMonth)))
                    .or(qFinancialRecord.year.eq(startYear).and(qFinancialRecord.month.eq(startMonth))
                            .and(qFinancialRecord.day.goe(startDay))));
        }

        // End Date 조건 추가
        if (endDate != null && !endDate.isEmpty()) {
            String[] endParts = endDate.split("-");
            int endYear = Integer.parseInt(endParts[0]);
            int endMonth = Integer.parseInt(endParts[1]);
            int endDay = Integer.parseInt(endParts[2]);

            predicate.and(qFinancialRecord.year.lt(endYear)
                    .or(qFinancialRecord.year.eq(endYear).and(qFinancialRecord.month.lt(endMonth)))
                    .or(qFinancialRecord.year.eq(endYear).and(qFinancialRecord.month.eq(endMonth))
                            .and(qFinancialRecord.day.loe(endDay))));
        }

        // 정렬 기준 설정
        OrderSpecifier<?> order = getOrderSpecifier(sortField, sortDirection, qFinancialRecord);

        List<FinancialRecord> records = queryFactory
                .selectFrom(qFinancialRecord)
                .where(predicate)
                .orderBy(order)
                .fetch();

        // Excel 생성 로직
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Financial Records");

            Row header = sheet.createRow(0);
            String[] headers = {"거래일", "거래 유형", "제품명", "수량", "단가", "공급 금액", "VAT", "총 금액"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (FinancialRecord record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.format("%04d-%02d-%02d", record.getYear(), record.getMonth(), record.getDay()));
                row.createCell(1).setCellValue(record.getTransactionType() == 1 ? "구매" : "판매");
                row.createCell(2).setCellValue(record.getProductName());
                row.createCell(3).setCellValue(record.getQuantity());
                row.createCell(4).setCellValue(record.getUnitPrice());
                row.createCell(5).setCellValue(record.getSupplyAmount());
                row.createCell(6).setCellValue(record.getVat());
                row.createCell(7).setCellValue(record.getSupplyAmount() + record.getVat());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생", e);
        }
    }



    private FinancialRecordDTO toDto(FinancialRecord record) {
        return new FinancialRecordDTO(
                record.getYear() + "-" + record.getMonth() + "-" + record.getDay(), // transactionDate
                record.getYear(),
                record.getMonth(),
                record.getDay(),
                record.getTransactionType() == 1 ? "구매" : "판매",
                record.getProductName(),
                record.getQuantity(),
                record.getUnitPrice(),
                record.getSupplyAmount(),
                record.getVat(),
                record.getSupplyAmount() + record.getVat()
        );
    }



}
