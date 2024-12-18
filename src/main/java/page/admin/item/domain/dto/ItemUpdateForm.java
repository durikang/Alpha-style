package page.admin.item.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ItemUpdateForm {

    @NotNull(message = "상품 ID는 필수 입력값입니다.")
    private Long id; // 상품 ID

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String itemName; // 상품명

    @NotNull(message = "가격은 필수 입력값입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price; // 가격

    @NotNull(message = "수량은 필수 입력값입니다.")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Integer quantity; // 수량

    private Boolean open; // 판매 여부 (true: 판매 중, false: 판매 중지)

    @NotEmpty(message = "지역 코드는 최소 1개 이상 입력해야 합니다.")
    private List<String> regions; // 등록 지역 (지역 코드 리스트)

    @NotBlank(message = "상품 종류 코드는 필수 입력값입니다.")
    private String itemType; // 상품 종류 (코드)

    @NotBlank(message = "배송 방식 코드는 필수 입력값입니다.")
    private String deliveryCode; // 배송 방식 (코드)

    // 이미지 관련 필드
    private MultipartFile mainImage; // 새로운 메인 이미지 파일 (수정 시)

    @Size(max = 4, message = "썸네일 이미지는 최대 4개까지 업로드할 수 있습니다.")
    private List<MultipartFile> thumbnails; // 새로운 썸네일 이미지 파일들 (수정 시)
}
