package gdg.hongik.mission.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Schema(description = "상품 정보")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(
            description = "서버가 생성하는 상품 ID",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(description = "상품명", example = "keyboard")
    @Column(name = "name", unique = true)
    private String itemName;


    @Schema(description = "개당 가격. 단위는 원", example = "30000")
    private int price;

    @Schema(description = "재고 수량", example = "20")
    @Column(name = "stock_quantity")
    private int quantity;

    @Enumerated(EnumType.STRING) // 반드시 STRING! (생략하면 ORDINAL이 되어 0, 1, 2 같은 순번이 저장됩니다)
    @Column(nullable = false)
    private Category category;
}