package page.admin.admin.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import page.admin.admin.manager.domain.Logo;

import java.util.Optional;

public interface LogoRepository extends JpaRepository<Logo, Long> {

    /**
     * 활성화된 로고를 반환합니다.
     */
    @Query("SELECT l FROM Logo l WHERE l.active = true")
    Optional<Logo> findActiveLogo();
}
