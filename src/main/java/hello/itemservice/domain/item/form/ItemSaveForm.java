package hello.itemservice.domain.item.form;

import hello.itemservice.domain.item.ItemType;
import hello.itemservice.domain.item.Region;
import hello.itemservice.domain.item.UploadFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ItemSaveForm {

    @NotBlank(message = "{item.itemName}")
    private String itemName;

    @NotNull(message = "{item.price}")
    private Long price;

    @NotNull(message = "{item.quantity}")
    private Integer quantity;

    private Boolean open;

    @NotNull(message = "{item.regions}")
    private List<Region> regions;

    @NotNull(message = "{item.itemType}")
    private ItemType itemType;

    @NotBlank(message = "{item.deliveryCode}")
    private String deliveryCode;

    // MultipartFile 추가
    private MultipartFile mainImage; // 메인 이미지
    private List<MultipartFile> thumbnails; // 썸네일 이미지
}

