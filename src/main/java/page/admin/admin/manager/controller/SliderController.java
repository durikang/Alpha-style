package page.admin.admin.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.admin.manager.domain.FileSettings;
import page.admin.admin.manager.domain.Slider;
import page.admin.admin.manager.exception.InvalidFileException;
import page.admin.admin.manager.service.SliderService;
import page.admin.admin.manager.service.FileSettingsService; // 추가
import page.admin.common.utils.Alert;
import page.admin.common.utils.exception.FileProcessingException;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/user/slider")
public class SliderController {

    private final SliderService sliderService;
    private final FileSettingsService fileSettingsService; // 추가

    // 슬라이더 목록 페이지
    @GetMapping
    public String viewSliders(Model model) {
        List<Slider> sliders = sliderService.getAllSliders();
        model.addAttribute("sliders", sliders);
        return "admin/manager/slider/sliderList";
    }

    // 슬라이더 추가 페이지
    @GetMapping("/add")
    public String addSliderForm(Model model) {
        model.addAttribute("slider", new Slider());

        // 파일 설정 정보 추가
        FileSettings settings = fileSettingsService.getSettings();
        long maxFileSizeMB = settings.getMaxFileSize() / (1024 * 1024);
        model.addAttribute("maxFileSizeMB", maxFileSizeMB);

        return "admin/manager/slider/sliderForm";
    }

    // 슬라이더 수정 페이지
    @GetMapping("/edit/{id}")
    public String editSliderForm(@PathVariable Long id, Model model) {
        Slider slider = sliderService.getSliderById(id);
        model.addAttribute("slider", slider);

        // 파일 설정 정보 추가
        FileSettings settings = fileSettingsService.getSettings();
        long maxFileSizeMB = settings.getMaxFileSize() / (1024 * 1024);
        model.addAttribute("maxFileSizeMB", maxFileSizeMB);

        return "admin/manager/slider/sliderForm";
    }

    // 슬라이더 저장 (추가 및 수정)
    @PostMapping("/save")
    public String saveSlider(
            @ModelAttribute Slider slider,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            sliderService.saveSlider(slider, imageFile);
            redirectAttributes.addFlashAttribute("alert",
                    new Alert("슬라이더가 성공적으로 저장되었습니다.", Alert.AlertType.SUCCESS));
        } catch (InvalidFileException e) {
            redirectAttributes.addFlashAttribute("alert",
                    new Alert("유효하지 않은 파일 형식입니다.", Alert.AlertType.ERROR));
        } catch (FileProcessingException e) { // 추가
            redirectAttributes.addFlashAttribute("alert",
                    new Alert("파일 크기가 너무 큽니다. 업로드 가능한 최대 크기는 "
                            + (fileSettingsService.getSettings().getMaxFileSize() / (1024 * 1024))
                            + "MB 입니다.", Alert.AlertType.ERROR));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alert",
                    new Alert("슬라이더 저장 중 알 수 없는 오류가 발생했습니다.", Alert.AlertType.ERROR));
        }
        return "redirect:/admin/user/slider";
    }


    // 슬라이더 삭제
    @PostMapping("/delete/{id}")
    public String deleteSlider(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sliderService.deleteSlider(id);
            redirectAttributes.addFlashAttribute("alert", new Alert("슬라이더가 성공적으로 삭제되었습니다.", Alert.AlertType.SUCCESS));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alert", new Alert("슬라이더 삭제 중 오류가 발생했습니다.", Alert.AlertType.ERROR));
        }
        return "redirect:/admin/user/slider";
    }
}
