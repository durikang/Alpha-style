package page.admin.admin.item.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class DeliveryCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_code_seq_generator")
    @SequenceGenerator(name = "delivery_code_seq_generator", sequenceName = "delivery_code_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "배송 코드는 필수 입력 사항입니다.")
    private String code;

    @NotBlank(message = "표시 이름은 필수 입력 사항입니다.")
    private String displayName;
}