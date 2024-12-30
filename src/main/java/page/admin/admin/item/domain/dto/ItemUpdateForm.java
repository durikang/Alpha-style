package page.admin.admin.item.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Data
public class ItemUpdateForm {

    @NotNull(message = "상품 ID는 필수 입력값입니다.")
    private Long itemId;

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String itemName;

    @NotNull(message = "매입가는 필수 입력값입니다.")
    @Min(value = 0, message = "매입가는 0 이상이어야 합니다.")
    private Integer purchasePrice;

    @NotNull(message = "판매가는 필수 입력값입니다.")
    @Min(value = 0, message = "판매가는 0 이상이어야 합니다.")
    private Integer salePrice;

    @NotNull(message = "수량은 필수 입력값입니다.")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Integer quantity;

    private Boolean open;

    @NotEmpty(message = "지역은 최소 1개 이상 입력해야 합니다.")
    private Set<String> regionCodes;

    @NotNull(message = "상품 종류는 필수 입력값입니다.")
    private Long itemType;

    @NotBlank(message = "배송 방식은 필수 입력값입니다.")
    private String deliveryCode;

    @NotNull(message = "메인 카테고리는 필수 입력값입니다.")
    private Long mainCategory;

    @NotNull(message = "세부 카테고리는 필수 입력값입니다.")
    private Long subCategory;

    private MultipartFile mainImage;
    private String mainImagePath;

    @Size(max = 4, message = "썸네일 이미지는 최대 4개까지만 업로드할 수 있습니다.")
    private List<MultipartFile> thumbnails;

    private List<String> thumbnailPaths;


    private String existingMainImage;
    private List<String> existingThumbnails;
}
