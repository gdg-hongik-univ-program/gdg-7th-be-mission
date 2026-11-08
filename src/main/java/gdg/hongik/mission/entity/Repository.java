package gdg.hongik.mission.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Repository extends JpaRepository<Product, Long> {

    List<Product> findByName(String name);
}
