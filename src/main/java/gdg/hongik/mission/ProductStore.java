package gdg.hongik.mission;

import java.util.ArrayList;
import java.util.List;

/**
 * 1주차에는 아직 데이터베이스(DB)와 Repository 계층을 배우지 않았기 때문에,
 * 서버 메모리(RAM)에 상품 데이터를 임시로 저장해 두는 저장소 클래스입니다.
 * (!서버를 재시작하면 메모리에 저장된 데이터는 모두 초기화됩니다!)
 */
public class ProductStore {

    // 등록된 상품들을 순서대로 보관하는 리스트입니다.
    // static으로 선언하여 소비자/관리자 컨트롤러 어디서든 동일한 상품 목록에 접근할 수 있습니다.
    public static List<Product> products = new ArrayList<>();

    // 상품이 새로 등록될 때마다 1씩 증가하며 고유한 상품 ID를 부여하기 위한 변수입니다.
    public static Long sequence = 1L;
}
