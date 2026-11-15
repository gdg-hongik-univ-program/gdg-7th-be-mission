package gdg.hongik.mission.dto;

import gdg.hongik.mission.entity.Category;

public record ProductRequest(String name, int price, int stockQuantity, Category category) {
}