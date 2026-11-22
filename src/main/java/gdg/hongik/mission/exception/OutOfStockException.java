package gdg.hongik.mission.exception;

public class OutOfStockException extends BusinessException {

    public OutOfStockException() {
        super(ErrorCode.INSUFFICIENT_STOCK);
    }
}