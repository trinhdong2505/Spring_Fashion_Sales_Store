package Spring_AdamStore.mapper;

import Spring_AdamStore.dto.basic.EntityBasic;
import Spring_AdamStore.dto.basic.ProductVariantBasic;
import Spring_AdamStore.entity.FileEntity;
import Spring_AdamStore.entity.ProductVariant;
import Spring_AdamStore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VariantMappingHelper {

    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final FileRepository fileRepository;

    public ProductVariantBasic getProductVariantBasic(Long variantId){
        return productVariantRepository.findById(variantId)
                .map(variant -> ProductVariantBasic.builder()
                        .id(variant.getId())
                        .color(getColor(variant.getColorId()))
                        .size(getSize(variant.getSizeId()))
                        .product(getProduct(variant.getProductId()))
                        .build())
                .orElse(null);
    }

    public String getImageUrl(Long imageId){
        return fileRepository.findById(imageId)
                .map(FileEntity::getImageUrl)
                .orElse(null);
    }

    public EntityBasic getSize(Long sizeId){
        return sizeRepository.findById(sizeId)
                .map(size -> new EntityBasic(size.getId(), size.getName()))
                .orElse(null);
    }

    public EntityBasic getColor(Long colorId){
        return colorRepository.findById(colorId)
                .map(color -> new EntityBasic(color.getId(), color.getName()))
                .orElse(null);
    }

    public EntityBasic getProduct(Long productId){
        return productRepository.findById(productId)
                .map(product -> new EntityBasic(product.getId(), product.getName()))
                .orElse(null);
    }


}
