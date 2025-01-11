package page.admin.admin.item.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.admin.admin.member.domain.Member;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long reviewId;
    private String username;
    private Double rating;
    private String comment;
    private LocalDateTime createdDate;
}
