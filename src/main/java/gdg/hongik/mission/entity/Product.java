package gdg.hongik.mission.entity;

import gdg.hongik.mission.global.exception.ConflictException;
import gdg.hongik.mission.global.message.ErrorMessage;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING) // 반드시 STRING (생략하면 ORDINAL이 되어 0, 1, 2 같은 순번이 저장됩니다)
    @Column(nullable = false)
    private Category category;
    private int price;
    private int stock;

    public Product(String name, Category category, int price, int stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public void addStock(int quantity) {
        stock += quantity;
    }

    public void deductStock(int quantity) {
        if (quantity > this.stock) {
            throw new ConflictException(ErrorMessage.INSUFFICIENT_STOCK);
        }
        stock -= quantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {return category; }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
}
