package page.admin.item.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ItemSaveForm {

    @NotBlank(message = "{item.itemName}")
    private String itemName; // 상품명

    @NotNull(message = "{item.price}")
    private Long price; // 가격

    @NotNull(message = "{item.quantity}")
    private Integer quantity; // 수량

    private Boolean open; // 판매 여부

    @NotNull(message = "{item.regions}")
    private List<Long> regions; // Region의 ID 리스트

    @NotNull(message = "{item.itemType}")
    private Long itemType; // ItemType의 ID

    @NotBlank(message = "{item.deliveryCode}")
    private String deliveryCode; // 배송 방식 코드

    // 파일 업로드 필드
    private MultipartFile mainImage; // 메인 이미지
    private List<MultipartFile> thumbnails; // 썸네일 이미지
}
