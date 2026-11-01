package gdg.hongik.mission;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductStore {

    private final List<Product> products = new ArrayList<>();

    public List<Product> getProducts() {
        return products;
    }
}
