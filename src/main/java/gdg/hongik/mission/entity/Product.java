package gdg.hongik.mission.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private int stock;

    public Product(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void addStock(int quantity) {
        stock += quantity;
    }

    public void deductStock(int quantity) {
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


    //상품 조회 dto
    public record GetProductResponse(Long id, String name, int price, int stock){}

    //상품 구매 dto
    public record CreateOrderRequest(Long productId, int quantity) { }
    public record CreateOrderResponse(List<OrderedItems> items, int totalPrice) { }

    public record OrderedItems(Long productId,
                               String productName,
                               int quantity,
                               int subtotal,
                               int remainingStock) { }

    //상품 생성 dto
    public record CreateProductRequest(String name, int price, int stock) { }
    public record CreateProductResponse(Long id, String name, int price, int stock) { }

    //재고 추가 dto
    public record AddStockRequest(Long productId, int quantity) { }
    public record AddStockResponse(Long id, String name, int price, int stock) { }

    //상품 삭제 dto
    public record DeleteProductRequest(Long id) { }
    public record DeleteProductResponse(Long id, String name, int price, int stock) { }
}
