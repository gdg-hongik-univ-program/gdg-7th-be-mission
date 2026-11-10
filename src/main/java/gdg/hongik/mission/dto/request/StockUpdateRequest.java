package gdg.hongik.mission.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockUpdateRequest {
    private Long productId;
    private int quantityToAdd;
}