package Spring_AdamStore.service;

import Spring_AdamStore.dto.request.VariantRequest;
import Spring_AdamStore.dto.request.VariantUpdateRequest;
import Spring_AdamStore.dto.response.ProductVariantResponse;
import Spring_AdamStore.entity.Color;
import Spring_AdamStore.entity.Product;
import Spring_AdamStore.entity.ProductVariant;
import Spring_AdamStore.entity.Size;

import java.util.List;
import java.util.Set;

public interface ProductVariantService {

    ProductVariantResponse findByProductAndColorAndSize(Long productId, Long colorId, Long sizeId);

    Set<ProductVariant> saveVariantByProduct(Long productId, List<VariantRequest> variantRequests);

    ProductVariantResponse updatePriceAndQuantity(Long id, VariantUpdateRequest request);

    void delete(Long id);

    ProductVariantResponse restore(long id);

    List<ProductVariant> findAllProductVariantByProductId(Long id);
}
