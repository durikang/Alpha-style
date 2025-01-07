package page.admin.admin.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.item.domain.UploadFile;
import page.admin.admin.manager.domain.Slider;
import page.admin.admin.manager.exception.InvalidFileException;
import page.admin.admin.manager.exception.SliderSaveException;
import page.admin.admin.manager.repository.SliderRepository;
import page.admin.admin.manager.service.SliderService;
import page.admin.common.utils.file.FileStore;

import java.io.IOException;
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
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                UploadFile uploadFile = fileStore.storeFile(imageFile);

                // 파일 저장이 실패했거나 유효하지 않다면 사용자 정의 예외 발생
                if (uploadFile == null || !fileStore.isValidFile(uploadFile)) {
                    throw new InvalidFileException("파일 형식이 유효하지 않습니다.");
                }

                slider.setImageUrl("/files/" + uploadFile.getStoreFileName());
            }
            return sliderRepository.save(slider);
        } catch (IOException e) {
            throw new SliderSaveException("파일 저장 중 오류 발생", e);
        }
    }



    @Override
    public void deleteSlider(Long id) {
        sliderRepository.deleteById(id);
    }
}
