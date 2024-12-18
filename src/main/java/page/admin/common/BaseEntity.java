package page.admin.common;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
}
