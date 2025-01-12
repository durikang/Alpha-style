package page.admin.admin.order.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductSalesDTOTest {

    @Test
    void testProductSalesDTOConstruction() {
        // Arrange: 테스트 데이터를 준비합니다.
        Long orderNo = 1L;
        String itemName = "Test Item";
        String username = "Test User";
        Integer quantity = 5;
        Integer salePrice = 1000;
        Double totalSalesAmount = 5000.0;

        // Act: DTO를 생성합니다.
        ProductSalesDTO dto = new ProductSalesDTO(orderNo, itemName, username, quantity, salePrice, totalSalesAmount);

        // Assert: 값이 예상대로 매핑되었는지 확인합니다.
        assertEquals(orderNo, dto.getOrderNo(), "OrderNo가 올바르지 않습니다.");
        assertEquals(itemName, dto.getItemName(), "ItemName이 올바르지 않습니다.");
        assertEquals(username, dto.getUsername(), "Username이 올바르지 않습니다.");
        assertEquals(quantity, dto.getQuantity(), "Quantity가 올바르지 않습니다.");
        assertEquals(salePrice, dto.getSalePrice(), "SalePrice가 올바르지 않습니다.");
        assertEquals(totalSalesAmount, dto.getTotalSalesAmount(), "TotalSalesAmount가 올바르지 않습니다.");
    }

    @Test
    void testDefaultConstructor() {
        // Arrange & Act: 기본 생성자를 사용합니다.
        ProductSalesDTO dto = new ProductSalesDTO();

        // Assert: 모든 필드가 초기화되어 있는지 확인합니다.
        assertNull(dto.getOrderNo(), "OrderNo가 null이어야 합니다.");
        assertNull(dto.getItemName(), "ItemName이 null이어야 합니다.");
        assertNull(dto.getUsername(), "Username이 null이어야 합니다.");
        assertNull(dto.getQuantity(), "Quantity가 null이어야 합니다.");
        assertNull(dto.getSalePrice(), "SalePrice가 null이어야 합니다.");
        assertNull(dto.getTotalSalesAmount(), "TotalSalesAmount가 null이어야 합니다.");
    }
}
