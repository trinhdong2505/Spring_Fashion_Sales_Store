package Spring_AdamStore.mapper;

import Spring_AdamStore.dto.response.ProductVariantResponse;
import Spring_AdamStore.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductMappingHelper {

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;
    private final VariantMappingHelper variantMappingHelper;

    public List<ProductVariantResponse> getVariant(Long productId){
        return productVariantRepository.findAllByProductId(productId)
                .stream().map(variant -> productVariantMapper.toProductVariantResponse(variant, variantMappingHelper))
                .toList();
    }
}
