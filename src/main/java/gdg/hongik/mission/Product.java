package gdg.hongik.mission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Product { // 상품 도메인 모델 클래스
    private Long productId;
    private String name;
    private int price;
    private int stockQuantity;

}