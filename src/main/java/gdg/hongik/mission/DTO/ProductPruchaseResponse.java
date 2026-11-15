package gdg.hongik.mission.DTO;

import java.util.List;


public record ProductPruchaseResponse (List<PurchasedProduct> products,int totalprice)
{
    public ProductPruchaseResponse() {
        this(null,0);
    }
}
