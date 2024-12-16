package hello.itemservice.domain.item;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class ItemType {
    private String code; // 상품 유형 코드
    private String description; // 설명

    public ItemType() {}

    // 특정 로직을 추가할 수 있음
    public boolean isBook() {
        return "BOOK".equals(this.code);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemType itemType = (ItemType) o;
        return Objects.equals(code, itemType.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

}
