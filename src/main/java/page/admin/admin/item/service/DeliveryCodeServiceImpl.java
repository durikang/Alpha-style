package page.admin.admin.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.admin.item.domain.DeliveryCode;
import page.admin.admin.item.repository.DeliveryCodeRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryCodeServiceImpl implements DeliveryCodeService {

    private final DeliveryCodeRepository deliveryCodeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryCode> findAll() {
        return deliveryCodeRepository.findAll();
    }

    @Override
    public void save(DeliveryCode newDeliveryMethod) {
        deliveryCodeRepository.save(newDeliveryMethod);
        log.info("새로운 배송 방식 추가: {}", newDeliveryMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeliveryCode> findById(Long id) {
        return deliveryCodeRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        if (deliveryCodeRepository.existsById(id)) {
            deliveryCodeRepository.deleteById(id);
            log.info("배송 방식 삭제: ID {}", id);
        } else {
            log.warn("삭제하려는 배송 방식이 존재하지 않습니다: ID {}", id);
            throw new IllegalArgumentException("삭제하려는 배송 방식이 존재하지 않습니다: ID " + id);
        }
    }
}