package gdg.hongik.mission.repository;

import gdg.hongik.mission.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);
    boolean existsById(Long id);
    boolean existsByName(String name);

}
