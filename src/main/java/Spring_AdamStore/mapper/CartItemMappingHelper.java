package Spring_AdamStore.mapper;

import Spring_AdamStore.dto.basic.ProductVariantBasic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartItemMappingHelper {

    private final VariantMappingHelper variantMappingHelper;

    public ProductVariantBasic getProductVariantBasic(Long variantId){
        return variantMappingHelper.getProductVariantBasic(variantId);
    }
}
