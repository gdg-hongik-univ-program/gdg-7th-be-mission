package gdg.hongik.mission.exception;

// { "code": "PRODUCT_NOT_FOUND", "message": "없는 상품이에요." }
public record ErrorResponse(String code, String message) {

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }
}