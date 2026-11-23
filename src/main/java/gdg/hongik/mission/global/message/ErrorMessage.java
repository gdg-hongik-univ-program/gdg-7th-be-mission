package gdg.hongik.mission.global.message;

public final class ErrorMessage {
    private ErrorMessage() {
    }

    public static final String PRODUCT_NOT_FOUND = "상품을 찾을 수 없습니다.";
    public static final String PRODUCT_NAME_REQUIRED = "상품명은 비어 있을 수 없습니다.";
    public static final String DUPLICATE_REQUEST_PRODUCT_NAME = "요청 목록에 중복된 상품명이 있습니다";
    public static final String PRODUCT_ALREADY_EXISTS = "이미 등록된 상품명입니다";
    public static final String INSUFFICIENT_STOCK = "재고가 부족합니다.";
    public static final String INTERNAL_SERVER_ERROR = "서버 오류가 발생했습니다.";
}
