package gdg.hongik.mission.service;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 상품과 관련된 핵심 비즈니스 로직(검증, 재고 계산, 구매 처리 등)을 담당하는 Service 계층입니다.
 * - @Service: 이 클래스를 스프링 빈(Bean)으로 등록하여 스프링이 객체를 생성하고 관리하도록 합니다.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 생성자를 통해 스프링이 관리하는 ProductRepository 빈(Bean)을 주입받습니다. (의존성 주입, DI)
     * 우리가 직접 new ProductRepository()를 호출하지 않아도 스프링이 알아서 연결해 줍니다.
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * [관리자] 1. 상품 등록 비즈니스 로직
     */
    public Product registerProduct(Product request) {
        // 1) 동일한 이름의 상품이 이미 DB에 존재하는지 조회합니다.
        Product existingProduct = productRepository.findByName(request.getName());
        if (existingProduct != null) {
            throw new RuntimeException("이미 존재하는 상품입니다.");
        }

        // 2) 새로운 상품 엔티티를 생성합니다. (ID는 DB에 저장될 때 자동 생성됩니다.)
        Product newProduct = new Product(
                request.getName(),
                request.getPrice(),
                request.getStock()
        );

        // 3) Repository를 통해 DB에 저장하고, 저장된 엔티티(ID가 부여된 상태)를 반환합니다.
        return productRepository.save(newProduct);
    }

    /**
     * [관리자] 2. 재고 추가 비즈니스 로직
     */
    public Product addStock(Product request) {
        // 1) JpaRepository가 기본 제공하는 findById()로 상품을 찾습니다.
        //    조회 결과가 없을 수도 있기 때문에 Optional<Product> 형태로 반환됩니다.
        Optional<Product> optionalProduct = productRepository.findById(request.getId());

        // 2) 상품이 존재하지 않으면(isEmpty) 예외를 발생시킵니다.
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("상품을 찾을 수 없습니다.");
        }

        // 3) Optional 상자에서 실제 Product 객체를 꺼낸 뒤 재고를 추가합니다.
        //    (참고: 아직 트랜잭션을 적용하지 않아 메모리 상의 객체 값만 바뀌며, DB 반영은 3주차에 다룹니다.)
        Product product = optionalProduct.get();
        product.setStock(product.getStock() + request.getStock());
        return product;
    }

    /**
     * [관리자] 3. 상품 삭제 비즈니스 로직
     */
    public List<Product> deleteProducts(List<Long> productIds) {
        // 1) 삭제 요청받은 상품 ID들을 하나씩 확인합니다.
        for (Long id : productIds) {
            Optional<Product> optionalProduct = productRepository.findById(id);

            // 삭제하려는 상품이 DB에 존재하지 않으면 예외를 발생시킵니다.
            if (optionalProduct.isEmpty()) {
                throw new RuntimeException("삭제할 상품을 찾을 수 없습니다.");
            }

            // 존재하면 DB에서 해당 상품을 삭제합니다.
            productRepository.deleteById(id);
        }

        // 2) 삭제 후 DB에 남아있는 전체 상품 목록을 조회하여 반환합니다.
        return productRepository.findAll();
    }

    /**
     * [소비자] 1. 상품명으로 단건 조회 비즈니스 로직
     */
    public Product getProductByName(String name) {
        Product product = productRepository.findByName(name);

        // 일치하는 상품이 없으면 예외를 발생시킵니다.
        if (product == null) {
            throw new RuntimeException("상품을 찾을 수 없습니다.");
        }

        return product;
    }

    /**
     * [소비자] 2. 상품 구매 비즈니스 로직
     */
    public Map<String, Object> purchaseProducts(List<Product> requests) {
        int totalPrice = 0; // 전체 구매 금액을 누적할 변수
        List<Product> purchasedProducts = new ArrayList<>(); // 구매 완료된 상품 내역을 담을 리스트

        // 1) 클라이언트가 구매 요청한 항목들을 하나씩 처리합니다.
        for (Product request : requests) {
            Optional<Product> optionalProduct = productRepository.findById(request.getId());

            // 상품을 찾지 못한 경우 예외를 발생시킵니다.
            if (optionalProduct.isEmpty()) {
                throw new RuntimeException("상품을 찾을 수 없습니다.");
            }

            Product targetProduct = optionalProduct.get();

            // 재고가 구매 수량보다 부족한 경우 예외를 발생시킵니다.
            if (targetProduct.getStock() < request.getStock()) {
                throw new RuntimeException("재고가 부족합니다.");
            }

            // 상품 재고에서 구매 수량만큼 차감합니다.
            targetProduct.setStock(targetProduct.getStock() - request.getStock());

            // 해당 상품의 소비 금액(단가 * 구매 수량)을 계산하고 총 구매 금액에 더합니다.
            int itemTotalPrice = targetProduct.getPrice() * request.getStock();
            totalPrice += itemTotalPrice;

            // 구매 내역 리스트에 추가합니다.
            purchasedProducts.add(new Product(
                    targetProduct.getId(),
                    targetProduct.getName(),
                    itemTotalPrice,
                    request.getStock()
            ));
        }

        // 2) 총 구매 금액과 구매한 상품 목록을 Map에 담아 반환합니다.
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalPrice", totalPrice);
        response.put("purchasedProducts", purchasedProducts);
        return response;
    }
}
