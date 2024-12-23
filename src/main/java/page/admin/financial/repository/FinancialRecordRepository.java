package page.admin.financial.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import page.admin.financial.domain.FinancialRecord;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    @Query("SELECT f FROM FinancialRecord f WHERE " +
            "(:keyword IS NULL OR f.productName LIKE %:keyword%) AND " +
            "(:transactionType IS NULL OR f.transactionType = :transactionType)")
    Page<FinancialRecord> findByFilters(@Param("keyword") String keyword,
                                        @Param("transactionType") Integer transactionType,
                                        Pageable pageable);
}
