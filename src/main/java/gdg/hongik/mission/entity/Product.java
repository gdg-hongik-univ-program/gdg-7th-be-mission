package gdg.hongik.mission.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor // JPA는 기본 생성자가 필요합니다.
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;          // 상품 번호 (DB가 자동 생성)


    @Column(nullable = false, unique = true)
    private String itemName;  // 상품 이름


    private long price;       // 1개 가격
    private long quantity;    // 남은 재고


    @Enumerated(EnumType.STRING) // 반드시 STRING! (생략하면 ORDINAL이 되어 0, 1, 2 같은 순번이 저장됩니다)
    @Column(nullable = false)
    private Category category;

    
    public Product(String itemName, long price, long quantity, Category category) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    // setter 대신 의미 있는 메서드로만 재고를 바꿉니다.
    public void addQuantity(long amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(long amount) {
        this.quantity -= amount;
    }
}
