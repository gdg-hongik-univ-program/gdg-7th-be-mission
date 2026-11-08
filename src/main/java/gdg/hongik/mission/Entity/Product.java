package gdg.hongik.mission.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor // JPA는 기본 생성자가 필요합니다.
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;       // 상품 번호 (DB가 자동 생성)

    private String name;   // 상품 이름
    private long price;    // 1개 가격
    private long stock;    // 남은 재고

    // id는 DB가 만들어 주므로 생성자에서 받지 않습니다.
    public Product(String name, long price, long stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}
