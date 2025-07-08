package Spring_AdamStore.mapper;

import Spring_AdamStore.dto.response.CartItemResponse;
import Spring_AdamStore.dto.response.OrderItemResponse;
import Spring_AdamStore.entity.CartItem;
import Spring_AdamStore.entity.OrderItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderItemMapper {

    @Mapping(target = "productVariant", expression = "java(context.getProductVariantBasic(orderItem.getProductVariantId()))")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem, @Context OrderItemMappingHelper context);

    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItemList, @Context OrderItemMappingHelper context);

}
