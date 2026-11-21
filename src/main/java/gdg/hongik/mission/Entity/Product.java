package gdg.hongik.mission.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemName;
    private int price;
    private int quantity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    public Product(String itemName, int price, int quantity, Category category) {
        this.quantity = quantity;
        this.price = price;
        this.itemName = itemName;
        this.category = category;
    }

    public Product() {

    }
}
