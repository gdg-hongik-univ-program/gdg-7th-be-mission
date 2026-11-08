package gdg.hongik.mission.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Product {

    @Id
    private Long id;
    private String name;
    private int price;
    private int stock;

    public Product(Long id, String name, int price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void addStock(int quantity) {
        stock += quantity;
    }

    public void removeStock(int quantity) {
        stock -= quantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public record GetProductResponse(Long id, String name, int price, int stock){}

    public record OrderRequest(Long id, int quantity) { }

    public record CreateOrderResponse(Long productId,
                                   String productName,
                                   int quantity,
                                   int totalPrice,
                                   int remainingStock) {

    }
}
