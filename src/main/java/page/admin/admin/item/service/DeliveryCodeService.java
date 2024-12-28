package page.admin.admin.item.service;

import page.admin.admin.item.domain.DeliveryCode;

import java.util.List;
import java.util.Optional;

public interface DeliveryCodeService {
    List<DeliveryCode> findAll(); // 모든 배송 방식 조회

    void save(DeliveryCode newDeliveryMethod);

    Optional<DeliveryCode> findById(Long id);

    void deleteById(Long id);
}
