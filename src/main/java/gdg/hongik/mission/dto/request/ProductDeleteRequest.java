package gdg.hongik.mission.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductDeleteRequest(
        @NotBlank(message = "삭제할 상품명은 필수입니다.") String itemName) {
}
