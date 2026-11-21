package gdg.hongik.mission.DTO.Response;

import java.util.List;


public record ProductPruchaseResponse ( long totalprice, List<PurchasedProduct> items)
{
    public ProductPruchaseResponse() {
        this(0, null);
    }
}
