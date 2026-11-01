package gdg.hongik.mission;

import java.util.ArrayList;
import java.util.List;

public class ProductStore { // 리스트와 ID 생성 시퀀스를 가지는 메모리 저장소 클래스
    public static final List<Product> products = new ArrayList<>();
    private static long sequence = 100L; // ID 자동 증가를 위한 시퀀스 (초기값 100)

    public static synchronized long nextId() {
        return ++sequence;
    }
}