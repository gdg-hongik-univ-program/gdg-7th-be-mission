package gdg.hongik.mission.Repository;

import gdg.hongik.mission.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 이름으로 조회 (메서드 이름만으로 JPA가 쿼리를 만들어 줍니다)
    Optional<Product> findByName(String name);

    // 이름 중복 검사
    boolean existsByName(String name);

    // findById, findAll, save, deleteById 등은 JpaRepository가 기본 제공
}
