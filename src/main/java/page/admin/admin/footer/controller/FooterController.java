package page.admin.admin.footer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/footer")
public class FooterController {

    // 이용약관 페이지
    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "common/footer/termsOfService";
    }

    // 개인정보처리방침 페이지
    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "common/footer/privacyPolicy";
    }

    // FAQ 페이지
    @GetMapping("/faq")
    public String faq() {
        return "common/footer/faq";
    }

    // 고객센터 페이지
    @GetMapping("/customer-center")
    public String customerCenter() {
        return "common/footer/customerCenter";
    }

    // 사이트맵 페이지
    @GetMapping("/sitemap")
    public String sitemap() {
        return "common/footer/sitemap";
    }
}
