package page.admin.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.item.domain.DeliveryCode;
import page.admin.item.repository.DeliveryCodeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryCodeServiceImpl implements DeliveryCodeService {

    private final DeliveryCodeRepository deliveryCodeRepository;

    @Override
    public List<DeliveryCode> getAllDeliveryCodes() {
        return deliveryCodeRepository.findAll();
    }
}
