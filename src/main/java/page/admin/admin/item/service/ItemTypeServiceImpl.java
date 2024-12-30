package page.admin.admin.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.admin.item.domain.ItemType;
import page.admin.admin.item.repository.ItemTypeRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemTypeServiceImpl implements ItemTypeService {

    private final ItemTypeRepository itemTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemType> findAll() {
        return itemTypeRepository.findAll();
    }

    @Override
    public void save(ItemType newItemType) {
        itemTypeRepository.save(newItemType);
        log.info("새로운 상품 종류 추가: {}", newItemType);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ItemType> findById(Long id) {
        return itemTypeRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        if (itemTypeRepository.existsById(id)) {
            itemTypeRepository.deleteById(id);
            log.info("상품 종류 삭제: ID {}", id);
        } else {
            log.warn("삭제하려는 상품 종류가 존재하지 않습니다: ID {}", id);
            throw new IllegalArgumentException("삭제하려는 상품 종류가 존재하지 않습니다: ID " + id);
        }
    }
}