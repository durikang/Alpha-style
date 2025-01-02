package page.admin.common.utils.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import page.admin.admin.item.domain.ItemType;
import page.admin.admin.item.domain.Region;
import page.admin.common.utils.converter.StringToItemTypeConverter;
import page.admin.common.utils.converter.StringToRegionConverter;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final List<ItemType> itemTypes;
    private final List<Region> regions;

    public WebConfig(List<ItemType> itemTypes, List<Region> regions) {
        this.itemTypes = itemTypes;
        this.regions = regions;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 커스텀 컨버터 등록
        registry.addConverter(new StringToItemTypeConverter(itemTypes));
        registry.addConverter(new StringToRegionConverter(regions));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**") // "/admin/**" 경로에만 적용
                .excludePathPatterns(
                        "/admin",              // 관리자 로그인 페이지 (GET)
                        "/admin/",            // 관리자 로그인 페이지 (GET)
                        "/admin/login",       // 관리자 로그인 처리 (POST)
                        "/admin/logout",      // 관리자 로그아웃 페이지
                        "/static/admin/**",   // 관리자 전용 정적 리소스
                        "/static/user/**",    // 사용자 전용 정적 리소스
                        "/static/alert/**",   // 공통 Alert 정적 리소스
                        "/css/**",            // 공용 CSS
                        "/js/**",             // 공용 JS
                        "/images/**",         // 공용 이미지
                        "/fonts/**"           // 공용 폰트
                );
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns("/user/**")
                .excludePathPatterns(
                        "/user/css/**", "/user/js/**", "/user/img/**",
                        "/static/user/css/**", "/static/user/js/**", "/static/user/img/**"
                );
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 관리자 정적 리소스 매핑
        registry.addResourceHandler("/admin/css/**")
                .addResourceLocations("classpath:/static/admin/css/")
                .setCachePeriod(3600);
        registry.addResourceHandler("/admin/js/**")
                .addResourceLocations("classpath:/static/admin/js/")
                .setCachePeriod(3600);

        // 사용자 정적 리소스 매핑
        registry.addResourceHandler("/user/**")
                .addResourceLocations("classpath:/static/user/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/user/css/**")
                .addResourceLocations("classpath:/static/user/css/")
                .setCachePeriod(3600);
        registry.addResourceHandler("/user/js/**")
                .addResourceLocations("classpath:/static/user/js/")
                .setCachePeriod(3600);

        // 공통 Alert 리소스 매핑
        registry.addResourceHandler("/alert/css/**")
                .addResourceLocations("classpath:/static/alert/css/")
                .setCachePeriod(3600);
        registry.addResourceHandler("/alert/js/**")
                .addResourceLocations("classpath:/static/alert/js/")
                .setCachePeriod(3600);

        // 이미지 저장소 매핑
        registry.addResourceHandler("/images/**") // URL 경로
                .addResourceLocations("file:///C:/fileRepository/file/") // 로컬 디렉토리 경로
                .setCachePeriod(3600); // 캐싱 기간 설정 (초 단위)
    }
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }



}
