package gdg.hongik.mission.DTO.Request;


import gdg.hongik.mission.Entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequest (
        @NotBlank
        String itemName,
        @Positive
        int price,
        @PositiveOrZero
        int quantity,
        @NotNull
        Category category)
{}
