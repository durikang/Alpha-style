package page.admin.admin.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.item.domain.UploadFile;
import page.admin.admin.manager.domain.Slider;
import page.admin.admin.manager.repository.SliderRepository;
import page.admin.admin.manager.service.SliderService;
import page.admin.common.utils.file.FileStore;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SliderServiceImpl implements SliderService {

    private final SliderRepository sliderRepository;
    private final FileStore fileStore; // 파일 저장 유틸리티 추가

    @Override
    public List<Slider> getAllSliders() {
        return sliderRepository.findAllByOrderByOrderIndexAsc();
    }

    @Override
    public Slider getSliderById(Long id) {
        return sliderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slider not found with id: " + id));
    }

    @Override
    public Slider saveSlider(Slider slider, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            UploadFile uploadFile = fileStore.storeFile(imageFile);
            // imageUrl을 /files/{storeFileName} 형식으로 설정
            slider.setImageUrl("/files/" + uploadFile.getStoreFileName());
            // 필요 시, UploadFile 엔티티에 대한 추가 저장 로직
        }
        return sliderRepository.save(slider);
    }


    @Override
    public void deleteSlider(Long id) {
        sliderRepository.deleteById(id);
    }
}
