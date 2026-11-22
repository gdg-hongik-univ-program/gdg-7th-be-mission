package gdg.hongik.mission.exception;

import org.springframework.http.HttpStatus;


public enum ErrorCode {
        INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값이 잘못됐어요."),
        PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "없는 상품이에요."),
        DUPLICATE_PRODUCT_NAME(HttpStatus.CONFLICT, "같은 이름의 상품이 이미 있어요."),
        INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "재고가 모자라요."),
        METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 요청 방식이에요."),
        UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 Content-Type이에요."),
        NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주소예요."),
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했어요.");

        private final HttpStatus status;
        private final String message;

        ErrorCode(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        public HttpStatus getStatus() { return status; }
        public String getMessage() { return message; }
    }
