package Spring_AdamStore.mapper;

import Spring_AdamStore.dto.basic.EntityBasic;
import Spring_AdamStore.dto.basic.ProductVariantBasic;
import Spring_AdamStore.dto.request.ProductUpdateRequest;
import Spring_AdamStore.dto.request.VariantUpdateRequest;
import Spring_AdamStore.dto.response.ProductVariantResponse;
import Spring_AdamStore.entity.Product;
import Spring_AdamStore.entity.ProductVariant;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProductVariantMapper {

    @Mapping(target = "color", expression = "java(context.getColor(variant.getColorId()))")
    @Mapping(target = "size", expression = "java(context.getSize(variant.getSizeId()))")
    @Mapping(target = "imageUrl", expression = "java(context.getImageUrl(variant.getImageId()))")
    ProductVariantResponse toProductVariantResponse(ProductVariant variant, @Context VariantMappingHelper context);

    List<ProductVariantResponse> toProductVariantResponseList(List<ProductVariant> productVariantList, @Context VariantMappingHelper context);

    ProductVariantBasic toProductVariantBasic(ProductVariant productVariant);

}
