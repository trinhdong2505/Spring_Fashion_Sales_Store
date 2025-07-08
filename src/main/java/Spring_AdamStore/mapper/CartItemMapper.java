package Spring_AdamStore.mapper;

import Spring_AdamStore.dto.request.CartItemRequest;
import Spring_AdamStore.dto.request.ColorRequest;
import Spring_AdamStore.dto.response.CartItemResponse;
import Spring_AdamStore.dto.response.ColorResponse;
import Spring_AdamStore.entity.Cart;
import Spring_AdamStore.entity.CartItem;
import Spring_AdamStore.entity.Color;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CartItemMapper {

    @Mapping(target = "productVariantBasic", expression = "java(context.getProductVariantBasic(cartItem.getProductVariantId()))")
    CartItemResponse toCartItemResponse(CartItem cartItem, @Context CartItemMappingHelper context);

    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItemList, @Context CartItemMappingHelper context);

}
