package page.admin.admin.item.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.admin.admin.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "review_comment", nullable = false)
    private String reviewComment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    // 생성자 및 기타 메서드...
}
