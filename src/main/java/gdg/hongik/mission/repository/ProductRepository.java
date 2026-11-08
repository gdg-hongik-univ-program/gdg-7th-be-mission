package gdg.hongik.mission.repository;

import gdg.hongik.mission.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
=======
import org.springframework.stereotype.Repository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsById(Long id);
    boolean existsByName(String name);
>>>>>>> 09860731a42d36bf51fc05bc372199f0f9f16ccd
}
