package gdg.hongik.mission.repository;

import gdg.hongik.mission.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 데이터베이스에 접근하여 Product 엔티티를 저장, 조회, 삭제하는 Repository 인터페이스입니다.
 * - JpaRepository<Product, Long>을 상속(extends)받으면 save(), findById(), findAll(), deleteById() 등의
 *   기본적인 데이터베이스 조작 메서드를 우리가 직접 구현하지 않아도 자동으로 사용할 수 있습니다.
 *   (첫 번째 제네릭 Product는 다룰 엔티티 타입, 두 번째 Long은 엔티티의 @Id 필드 타입입니다.)
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 상품명(name)으로 상품을 조회하는 메서드입니다.
     * 메서드 이름을 "findBy + 필드명(Name)" 규칙에 맞춰 선언만 해두면,
     * JPA가 메서드 이름을 분석해 "SELECT * FROM product WHERE name = ?" 쿼리를 자동으로 만들어 실행합니다.
     */
    Product findByName(String name);
}
