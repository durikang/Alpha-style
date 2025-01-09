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
import page.admin.common.utils.exception.FileProcessingException;
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
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // FileStore에서 검증 및 저장
                UploadFile uploadFile = fileStore.storeFile(imageFile);

                // 파일 URL 설정
                slider.setImageUrl(fileStore.getFileUrl(uploadFile));
            } catch (FileProcessingException e) {
                // FileStore에서 발생한 예외를 SliderSaveException으로 변환
                throw new SliderSaveException("파일 처리 중 오류가 발생했습니다.", e);
            }
        }

        // Slider 저장
        return sliderRepository.save(slider);
    }


    @Override
    public void deleteSlider(Long id) {
        sliderRepository.deleteById(id);
    }
}
