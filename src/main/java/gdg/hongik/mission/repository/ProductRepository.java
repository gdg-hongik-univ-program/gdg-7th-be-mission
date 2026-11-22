package gdg.hongik.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gdg.hongik.mission.entity.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByItemName(String itemName);

    boolean existsByItemName(String itemName);
}
