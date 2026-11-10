package gdg.hongik.mission.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDeleteRequest {
    private List<Long> productIds;
}