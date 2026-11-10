package gdg.hongik.mission.dto.request;

import java.util.List;

public record ProductDeleteRequest(
        List<Long> productIds) {
}