package hello.itemservice.config;

import hello.itemservice.converter.StringToItemTypeConverter;
import hello.itemservice.converter.StringToRegionConverter;
import hello.itemservice.domain.item.ItemType;
import hello.itemservice.domain.item.Region;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                .addPathPatterns("/**") // 모든 경로에 적용
                .excludePathPatterns("/", "/login", "/logout", "/css/**", "/js/**", "/images/**"); // 예외 경로
    }
}
