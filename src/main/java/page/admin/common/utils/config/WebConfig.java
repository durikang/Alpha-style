package page.admin.common.utils.config;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final AdminInterceptor adminInterceptor;
    private final UserInterceptor userInterceptor;

    @Autowired
    public WebConfig(List<ItemType> itemTypes, List<Region> regions, AdminInterceptor adminInterceptor, UserInterceptor userInterceptor) {
        this.itemTypes = itemTypes;
        this.regions = regions;
        this.adminInterceptor = adminInterceptor;
        this.userInterceptor = userInterceptor;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 커스텀 컨버터 등록
        registry.addConverter(new StringToItemTypeConverter(itemTypes));
        registry.addConverter(new StringToRegionConverter(regions));
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /files/** 경로로 오는 요청을 C:/fileRepository/file/ 디렉토리로 매핑
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:///C:/fileRepository/file/");
    }
}
