package gdg.hongik.mission.DTO.Response;



import java.util.List;


public record ProductDeleteResponse (List<RemainingProduct> items)
{
    public ProductDeleteResponse() {
        this(null);
    }
}
