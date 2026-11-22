package gdg.hongik.mission.validator;

import gdg.hongik.mission.dto.request.ProductCreateRequest;
import gdg.hongik.mission.dto.request.ProductDeleteRequest;
import gdg.hongik.mission.dto.request.PurchaseItemRequest;
import gdg.hongik.mission.dto.request.StockUpdateRequest;
import gdg.hongik.mission.exception.InvalidRequestException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 요청 값 검증 전담. 잘못된 값이면 InvalidRequestException을 던짐
public final class ProductValidator {

    private static final int MAX_ITEM_NAME_LENGTH = 255;

    private ProductValidator() {}

    // 상품명 검증 후, 앞뒤 공백을 제거한 값을 돌려줌
    public static String itemName(String itemName) {
        if (itemName == null || itemName.isBlank()
                || itemName.trim().length() > MAX_ITEM_NAME_LENGTH) {
            throw new InvalidRequestException();
        }
        return itemName.trim();
    }

    public static void create(ProductCreateRequest request) {
        if (request == null
                || request.price() == null || request.price() < 0
                || request.quantity() == null || request.quantity() < 0
                || request.category() == null) {
            throw new InvalidRequestException();
        }
        itemName(request.itemName());
    }

    public static void stock(StockUpdateRequest request) {
        if (request == null || request.quantity() == null || request.quantity() < 1) {
            throw new InvalidRequestException();
        }
        itemName(request.itemName());
    }

    public static void delete(ProductDeleteRequest request) {
        if (request == null) {
            throw new InvalidRequestException();
        }
        itemName(request.itemName());
    }

    // null, 개수 1 미만, 중복 id 검사
    public static void purchase(List<PurchaseItemRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new InvalidRequestException();
        }
        Set<Long> seenIds = new HashSet<>();
        for (PurchaseItemRequest request : requests) {
            if (request == null || request.id() == null
                    || request.count() == null || request.count() < 1
                    || !seenIds.add(request.id())) {
                throw new InvalidRequestException();
            }
        }
    }
}