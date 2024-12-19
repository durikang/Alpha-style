package page.admin.item.service;

import page.admin.item.domain.DeliveryCode;

import java.util.List;

public interface DeliveryCodeService {
    List<DeliveryCode> getAllDeliveryCodes(); // 모든 배송 방식 조회
}
