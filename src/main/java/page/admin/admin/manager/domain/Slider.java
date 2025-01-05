package page.admin.admin.manager.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sliders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private String description;

    private Integer orderIndex;

    private Boolean active; // 슬라이더 활성화 여부
}
