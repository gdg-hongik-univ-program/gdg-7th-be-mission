package gdg.hongik.mission.exception;

public class InvalidRequestException extends BusinessException {

    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }
}