package page.admin.item.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import page.admin.item.domain.DeliveryCode;
import page.admin.item.domain.ItemType;
import page.admin.item.domain.Region;
import page.admin.item.domain.UploadFile;

import java.util.List;

@Data
public class ItemEditForm {

    @NotNull(message = "상품 ID는 필수 입력값입니다.")
    private Long itemId;

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String itemName;

    @NotNull(message = "가격은 필수 입력값입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Long price;

    @NotNull(message = "수량은 필수 입력값입니다.")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Integer quantity;

    private Boolean open;

    @NotEmpty(message = "지역 코드는 최소 1개 이상 입력해야 합니다.")
    private List<String> regionCodes; // 등록 지역 (지역 코드 리스트)

    @NotBlank(message = "상품 종류 코드는 필수 입력값입니다.")
    private String itemType; // 상품 종류 (코드)

    @NotBlank(message = "배송 방식 코드는 필수 입력값입니다.")
    private String deliveryCode; // 변경: String으로 정의

    private UploadFile mainImage;
    private List<UploadFile> thumbnails;

    private MultipartFile newMainImage;
    @Size(max = 4, message = "썸네일 이미지는 최대 4개까지 업로드할 수 있습니다.")
    private List<MultipartFile> newThumbnails;

}
