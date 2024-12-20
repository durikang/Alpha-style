package page.admin.item.domain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import page.admin.item.domain.UploadFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemUpdateForm {

    @NotNull(message = "상품 ID는 필수 입력값입니다.")
    private Long itemId;

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String itemName;

    @NotNull(message = "가격은 필수 입력값입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "수량은 필수 입력값입니다.")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Integer quantity;

    private Boolean open;

    @NotEmpty(message = "지역 코드는 최소 1개 이상 입력해야 합니다.")
    private List<String> regionCodes;

    @NotBlank(message = "상품 종류 코드는 필수 입력값입니다.")
    private String itemType;

    @NotBlank(message = "배송 방식 코드는 필수 입력값입니다.")
    private String deliveryCode;

    // MultipartFile 필드 추가
    private MultipartFile mainImage;

    @Size(min = 1, max = 4, message = "썸네일 이미지는 1개 이상 4개 이하로 업로드해야 합니다.")
    private List<MultipartFile> thumbnails;
}

