package gdg.hongik.mission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상품 정보를 담는 도메인 클래스입니다.
 * 1주차에서는 별도의 DTO 없이 이 클래스를 요청(@RequestBody)과 응답에 함께 활용합니다.
 * 아래 어노테이션이 어색하시다면 lombok에 대해 공부해보세요!
 */
@Getter // 모든 필드의 getter 메서드(getId, getName 등)를 자동 생성합니다. (객체 -> JSON 변환 시 필요)
@Setter // 모든 필드의 setter 메서드(setId, setName 등)를 자동 생성합니다.
@NoArgsConstructor // 기본 생성자(Product())를 자동 생성합니다. (JSON -> 객체 변환 시 필수)
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동 생성합니다.
public class Product {

    private Long id;      // 상품 고유 ID (ProductStore 클래스에서 자동 부여)
    private String name;  // 상품명
    private int price;    // 가격 (구매 응답에서는 해당 상품의 총 소비 금액으로 활용)
    private int stock;    // 재고 수량 (재고 추가/구매 요청에서는 추가·구매할 수량으로 활용)
}
