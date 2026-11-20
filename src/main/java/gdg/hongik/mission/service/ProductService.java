package gdg.hongik.mission.service;

import gdg.hongik.mission.common.BadRequestException;
import gdg.hongik.mission.common.NotFoundException;
import gdg.hongik.mission.dto.ProductRequest;
import gdg.hongik.mission.dto.ProductResponse;
import gdg.hongik.mission.dto.PurchaseRequest;
import gdg.hongik.mission.dto.PurchaseResponse;
import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.entity.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 상품의 조회, 등록, 재고 관리, 구매 및 삭제를 처리한다.
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;

    /**
     * 상품 서비스에 사용할 저장소를 설정한다.
     *
     * @param repository 상품을 조회하고 저장하는 저장소
     */
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * 등록된 전체 상품을 조회한다.
     *
     * @return 상품 응답 목록. 등록된 상품이 없으면 빈 목록
     */
    public List<ProductResponse> findAllProducts() {
        return repository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    /**
     * 상품명과 일치하는 상품 한 건을 조회한다.
     *
     * @param itemName 조회할 상품명
     * @return 조회한 상품의 정보
     * @throws NotFoundException 일치하는 상품이 없을 때
     */
    public ProductResponse findProductByItemName(String itemName) {
        return ProductResponse.from(findOneByItemName(itemName));
    }

    /**
     * 새 상품을 등록한다.
     *
     * @param request 검증을 마친 상품 등록 요청
     * @return 저장한 상품의 정보
     * @throws BadRequestException 같은 이름의 상품이 이미 등록되어 있을 때
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        String itemName = request.name();

        if (!repository.findByItemName(itemName).isEmpty()) {
            throw new BadRequestException("이미 존재하는 상품명입니다.");
        }

        Product product = new Product();
        product.setItemName(itemName);
        product.setPrice(request.price());
        product.setQuantity(request.stockQuantity());
        product.setCategory(request.category());

        return ProductResponse.from(repository.save(product));
    }

    /**
     * 상품명으로 상품을 찾아 현재 재고에 수량을 더한다.
     *
     * @param itemName 재고를 추가할 상품명
     * @param quantity 추가할 수량
     * @return 재고가 반영된 상품의 정보
     * @throws NotFoundException 해당 상품명의 상품이 없을 때
     * @throws BadRequestException 추가 후 재고가 정수 범위를 초과할 때
     */
    @Transactional
    public ProductResponse addStock(String itemName, Integer quantity) {
        Product product = findOneByItemName(itemName);
        if (quantity > Integer.MAX_VALUE - product.getQuantity()) {
            throw new BadRequestException("추가 후 재고가 허용 범위를 초과합니다.");
        }
        product.setQuantity(product.getQuantity() + quantity);

        return ProductResponse.from(product);
    }

    /**
     * 장바구니 상품을 일괄 구매하고 재고를 차감한다.
     * 처리 중 예외가 발생하면 이번 요청에서 차감한 재고가 롤백된다.
     *
     * @param items 검증을 마친 구매 요청 목록
     * @return 항목별 구매 내역과 총 구매 금액
     * @throws NotFoundException 구매 대상 상품이 하나라도 없을 때
     * @throws BadRequestException 상품 ID가 중복되거나 재고가 부족하거나 총액이 허용 범위를 초과할 때
     */
    @Transactional
    public PurchaseResponse purchaseProducts(List<PurchaseRequest> items) {
        Set<Long> selectedIds = new HashSet<>();
        List<PurchaseResponse.PurchaseItemResponse> purchasedItems = new ArrayList<>();
        long totalPrice = 0L;

        for (PurchaseRequest item : items) {
            if (!selectedIds.add(item.id())) {
                throw new BadRequestException("같은 상품은 하나의 항목으로 보내주세요.");
            }

            Product product = repository.findById(item.id())
                    .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));

            int count = item.count();

            if (product.getQuantity() < count) {
                throw new BadRequestException("재고가 부족합니다.");
            }

            long itemPrice = (long) product.getPrice() * count;
            if (itemPrice > Long.MAX_VALUE - totalPrice) {
                throw new BadRequestException("총 구매 금액이 허용 범위를 초과합니다.");
            }
            totalPrice += itemPrice;
            product.setQuantity(product.getQuantity() - count);

            purchasedItems.add(new PurchaseResponse.PurchaseItemResponse(
                    product.getItemName(),
                    count,
                    itemPrice
            ));
        }

        return new PurchaseResponse(totalPrice, purchasedItems);
    }

    /**
     * 요청한 ID에 해당하는 상품을 삭제한다. 같은 ID가 여러 번 들어오면 한 번만 처리한다.
     *
     * @param ids 삭제할 상품 ID 목록
     * @return 삭제 후 남은 전체 상품 목록
     * @throws NotFoundException 삭제 대상 상품이 하나라도 없을 때
     */
    @Transactional
    public List<ProductResponse> deleteProducts(List<Long> ids) {
        Set<Long> uniqueIds = new HashSet<>(ids);
        List<Product> products = repository.findAllById(uniqueIds);

        if (products.size() != uniqueIds.size()) {
            throw new NotFoundException("삭제할 상품을 찾을 수 없습니다.");
        }

        repository.deleteAll(products);
        return findAllProducts();
    }

    private Product findOneByItemName(String itemName) {
        return repository.findByItemName(itemName).stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));
    }
}
