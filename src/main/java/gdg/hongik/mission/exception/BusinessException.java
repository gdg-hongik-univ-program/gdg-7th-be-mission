package gdg.hongik.mission.exception;

// 서비스에서 던지는 모든 예외의 부모.
// RuntimeException이라서 던지면 @Transactional이 롤백함
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}