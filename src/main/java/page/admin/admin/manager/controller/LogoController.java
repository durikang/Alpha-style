package page.admin.admin.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.manager.domain.Logo;
import page.admin.admin.manager.service.LogoService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/user/logo")
public class LogoController {

    private final LogoService logoService;

    @GetMapping
    public String listLogos(Model model) {
        model.addAttribute("logos", logoService.getAllLogos());
        return "admin/manager/logo/list";
    }

    @PostMapping("/upload")
    public String uploadLogo(
            @RequestParam("logoName") String logoName,
            @RequestParam("logoFile") MultipartFile logoFile,
            Model model) {
        logoService.saveLogo(logoName, logoFile);
        return "redirect:/admin/user/logo";
    }

    @PostMapping("/delete/{id}")
    public String deleteLogo(@PathVariable Long id) {
        logoService.deleteLogo(id);
        return "redirect:/admin/user/logo";
    }
}
