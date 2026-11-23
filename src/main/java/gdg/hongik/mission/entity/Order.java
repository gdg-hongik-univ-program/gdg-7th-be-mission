package gdg.hongik.mission.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @OrderColumn(name = "item_position")
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addItem(Product product, int quantity) {
        OrderItem item = new OrderItem(this, product, quantity);
        orderItems.add(item);
        totalPrice += item.getSubtotal();
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}
