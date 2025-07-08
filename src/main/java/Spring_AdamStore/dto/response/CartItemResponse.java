package Spring_AdamStore.dto.response;

import Spring_AdamStore.dto.basic.ProductVariantBasic;
import jakarta.persistence.JoinColumn;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;

    private Double price;
    private Integer quantity;

    private ProductVariantBasic productVariantBasic;
}
