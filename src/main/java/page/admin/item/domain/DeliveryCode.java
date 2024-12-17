package page.admin.item.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DeliveryCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_code_seq_generator")
    @SequenceGenerator(name = "delivery_code_seq_generator", sequenceName = "delivery_code_seq", allocationSize = 1)
    private Long id;

    private String code;          // 배송 상태 코드
    private String displayName;   // 화면 표시 이름
}
