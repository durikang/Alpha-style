package page.admin.utils.converter;
import page.admin.item.domain.Region;
import org.springframework.core.convert.converter.Converter;
import java.util.List;

public class StringToRegionConverter implements Converter<String, Region> {

    private List<Region> regions;

    public StringToRegionConverter(List<Region> regions) {
        this.regions = regions;
    }

    @Override
    public Region convert(String source) {
        return regions.stream()
                .filter(region -> region.getCode().equals(source))
                .findFirst()
                .orElse(null);  // 해당 코드가 없으면 null 반환
    }
}