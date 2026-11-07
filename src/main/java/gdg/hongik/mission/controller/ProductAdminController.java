package gdg.hongik.mission.controller;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자(Admin)용 상품 관리 API 요청을 처리하는 컨트롤러입니다.
 * 2주차부터는 컨트롤러가 직접 데이터를 처리하지 않고, 요청을 받아 ProductService에게 작업을 위임합니다.
 */
@RestController
@RequestMapping("/admin/products")
public class ProductAdminController {

    private final ProductService productService;

    /**
     * 생성자를 통해 스프링이 관리하는 ProductService 빈(Bean)을 주입받습니다.
     */
    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 1. 상품 등록 API (POST /admin/products)
     */
    @PostMapping
    public Product registerProduct(@RequestBody Product request) {
        return productService.registerProduct(request);
    }

    /**
     * 2. 재고 추가 API (PATCH /admin/products/stock)
     */
    @PatchMapping("/stock")
    public Product addStock(@RequestBody Product request) {
        return productService.addStock(request);
    }

    /**
     * 3. 상품 삭제 API (DELETE /admin/products)
     */
    @DeleteMapping
    public List<Product> deleteProducts(@RequestBody List<Long> productIds) {
        return productService.deleteProducts(productIds);
    }
}
