package gdg.hongik.mission;

import lombok.Getter;

@Getter
public class Product {

    private final Long id;
    private final String name;
    private final int price;
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
}
