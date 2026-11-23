package gdg.hongik.mission.service;

import gdg.hongik.mission.dto.CreateOrderRequest;
import gdg.hongik.mission.dto.CreateOrderResponse;
import gdg.hongik.mission.dto.OrderedItems;
import gdg.hongik.mission.entity.Order;
import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.global.exception.NotFoundException;
import gdg.hongik.mission.global.message.ErrorMessage;
import gdg.hongik.mission.repository.OrderRepository;
import gdg.hongik.mission.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    //상품 구매
    @Transactional
    public CreateOrderResponse orderProduct(List<CreateOrderRequest> requests){
        Order order = new Order();
        List<OrderedItems> items = new ArrayList<>();

        for(CreateOrderRequest request : requests){

            //상품 검증
            Product targetProduct = productRepository.findById(request.productId())
                    .orElseThrow(()-> new NotFoundException(ErrorMessage.PRODUCT_NOT_FOUND));

            //재고 차감
            targetProduct.deductStock(request.quantity());
            order.addItem(targetProduct, request.quantity());
            //items 에 추가
            int targetPrice = request.quantity() * targetProduct.getPrice();
            items.add(new OrderedItems(
                    targetProduct.getId(),
                    targetProduct.getName(),
                    request.quantity(),
                    targetPrice,
                    targetProduct.getStock()
            ));
        }

        orderRepository.save(order);
        return new CreateOrderResponse(items, order.getTotalPrice());
    }
}
