package hello.itemservice.converter;

import hello.itemservice.domain.item.ItemType;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

public class StringToItemTypeConverter implements Converter<String, ItemType> {
    private List<ItemType> itemTypes;

    public StringToItemTypeConverter(List<ItemType> itemTypes) {
        this.itemTypes = itemTypes;
    }

    @Override
    public ItemType convert(String source) {
        return itemTypes.stream()
                .filter(itemType -> itemType.getCode().equals(source))
                .findFirst()
                .orElse(null);  // 해당 코드가 없으면 null 반환
    }
}
