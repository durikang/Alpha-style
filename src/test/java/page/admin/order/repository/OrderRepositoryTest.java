package page.admin.order.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import page.admin.order.domain.dto.OrderSummaryDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 테스트 후 데이터 롤백
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testFindOrderSummariesWithPaging() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderSummaryDTO> summaries = orderRepository.findOrderSummariesWithPaging(pageable);

        assertNotNull(summaries);
        assertTrue(summaries.hasContent());

        summaries.forEach(summary -> {
            System.out.println("Item ID: " + summary.getItemId());
            System.out.println("Item Name: " + summary.getItemName());
            System.out.println("Total Quantity: " + summary.getTotalQuantity());
            System.out.println("Total Amount: " + summary.getFormattedTotalAmount());
        });
    }


}