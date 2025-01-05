package page.admin.admin.manager.service;

import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.manager.domain.Slider;

import java.util.List;

public interface SliderService {
    List<Slider> getAllSliders();
    Slider getSliderById(Long id);
    Slider saveSlider(Slider slider, MultipartFile imageFile);
    void deleteSlider(Long id);
}
