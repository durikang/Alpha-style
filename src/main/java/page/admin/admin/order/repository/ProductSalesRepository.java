package page.admin.admin.order.repository;

import org.springframework.data.repository.Repository;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.repository.custom.ProductSalesRepositoryCustom;

public interface ProductSalesRepository extends Repository<OrderDetail, Long>, ProductSalesRepositoryCustom {

}
