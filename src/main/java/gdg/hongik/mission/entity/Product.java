package gdg.hongik.mission.entity;

<<<<<<< HEAD
import jakarta.persistence.Entity;
=======

import gdg.hongik.mission.controller.ProductAdminController;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
>>>>>>> 09860731a42d36bf51fc05bc372199f0f9f16ccd
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Product {

    @Id
<<<<<<< HEAD
=======
    @GeneratedValue(strategy = GenerationType.IDENTITY)
>>>>>>> 09860731a42d36bf51fc05bc372199f0f9f16ccd
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

<<<<<<< HEAD
=======

>>>>>>> 09860731a42d36bf51fc05bc372199f0f9f16ccd
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

<<<<<<< HEAD
    public record GetProductResponse(Long id, String name, int price, int stock){}

    public record OrderRequest(Long id, int quantity) { }

    public record CreateOrderResponse(Long productId,
                                   String productName,
                                   int quantity,
                                   int totalPrice,
                                   int remainingStock) {

=======

    public record CreateProductRequest(Long id, String name, int price, int stock) {
    }

    public record QuantityRequest(Long id, String name, int price, int stock) {
    }

    public record ProductResponse(Long id, String name, int price, int stock) {
>>>>>>> 09860731a42d36bf51fc05bc372199f0f9f16ccd
    }
}
