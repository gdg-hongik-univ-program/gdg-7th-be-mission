package gdg.hongik.mission.dto;

import java.util.List;

public record DeleteProductsRequest(List<Long> ids) {
}
