package gdg.hongik.mission.DTO;



import java.util.List;


public record ProductDeleteResponse (List<RemainingProduct> items)
{
    public ProductDeleteResponse() {
        this(null);
    }
}
