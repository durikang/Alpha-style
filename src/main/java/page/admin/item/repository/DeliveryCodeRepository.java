package page.admin.item.repository;

import page.admin.item.domain.DeliveryCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryCodeRepository extends JpaRepository<DeliveryCode, Long> {
    Optional<DeliveryCode> findByCode(String code);
}
