package gdg.hongik.mission.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateRequest {
    private String name;
    private int price;
    private int stockQuantity;
}