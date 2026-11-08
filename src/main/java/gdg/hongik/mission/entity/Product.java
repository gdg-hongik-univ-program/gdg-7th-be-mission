package gdg.hongik.mission.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 데이터베이스의 테이블과 매핑되는 상품 Entity 클래스입니다.
 * - @Entity: 이 클래스를 JPA가 관리하는 엔티티로 지정합니다. 데이터베이스에 "product" 테이블이 자동으로 생성됩니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor // JPA가 엔티티 객체를 생성할 때 기본 생성자가 반드시 필요합니다.
@AllArgsConstructor
public class Product {

    /**
     * - @Id: 이 필드를 테이블의 기본 키(Primary Key, PK)로 지정합니다.
     * - @GeneratedValue(strategy = GenerationType.IDENTITY):
     *   1주차에서 ProductStore.sequence++로 직접 ID를 1씩 증가시켰던 것을,
     *   이제는 데이터베이스가 알아서 1씩 증가시키며(AUTO_INCREMENT) 번호를 부여하도록 위임합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;      // 상품 고유 ID

    private String name;  // 상품명
    private int price;    // 가격
    private int stock;    // 재고 수량

    // 상품 등록 시 ID는 DB가 자동 생성하므로, 이름·가격·재고만 받아 객체를 생성할 때 사용하는 생성자입니다.
    public Product(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}
