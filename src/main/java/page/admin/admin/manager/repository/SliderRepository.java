package page.admin.admin.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import page.admin.admin.manager.domain.Slider;

import java.util.List;

@Repository
public interface SliderRepository extends JpaRepository<Slider, Long> {

    List<Slider> findAllByOrderByOrderIndexAsc();
}