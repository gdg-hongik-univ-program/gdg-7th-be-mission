package gdg.hongik.mission.dto;

import java.util.List;

public record PurchaseResponse(long totalAmount, List<PurchaseItemResponse> items) {

    public record PurchaseItemResponse(String name, long quantity, long amount) {
    }
}