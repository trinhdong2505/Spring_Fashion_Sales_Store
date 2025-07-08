package Spring_AdamStore.dto.response;

import Spring_AdamStore.dto.basic.ProductVariantBasic;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long id;

    private Double unitPrice;
    private Integer quantity;

    private ProductVariantBasic productVariant;
}
