package gdg.hongik.mission.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gdg.hongik.mission.entity.Product;

// JpaRepository를 상속받으면 Spring Data JPA가 자동으로 구현체를 만들어 Spring Bean으로 등록해 줍니다
// (IoC/DI 활용 가능).
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 상품명으로 단건 조회 (컨트롤러의 1-1 상품 조회 로직 대체용)
    Optional<Product> findByName(String name);

    // 상품명 중복 여부 확인 (컨트롤러의 2-1 상품 등록 중복 검증 로직 대체용)
    boolean existsByName(String name);
}