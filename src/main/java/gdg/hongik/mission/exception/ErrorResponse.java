package gdg.hongik.mission.exception;

public record ErrorResponse(
        int status,
        String code,
        String message) {

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.name(),
                message);
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode, errorCode.getMessage());
    }
}
