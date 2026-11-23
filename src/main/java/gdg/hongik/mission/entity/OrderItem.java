package gdg.hongik.mission.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;
    private String productName;

    private int quantity;
    private int orderPrice; // 주문 당시의 상품 가격

    OrderItem(Order order, Product product, int quantity) {
        this.order = order;
        this.productId = product.getId();
        this.productName = product.getName();
        this.quantity = quantity;
        this.orderPrice = product.getPrice();
    }

    int getSubtotal() {
        return orderPrice * quantity;
    }
}
