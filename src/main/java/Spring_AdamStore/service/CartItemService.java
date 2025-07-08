
package Spring_AdamStore.service;

import Spring_AdamStore.dto.request.CartItemRequest;
import Spring_AdamStore.dto.request.CartItemUpdateRequest;
import Spring_AdamStore.dto.request.ColorRequest;
import Spring_AdamStore.dto.response.CartItemResponse;
import Spring_AdamStore.dto.response.ColorResponse;
import Spring_AdamStore.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CartItemService {

    CartItemResponse create(CartItemRequest request);

    CartItemResponse fetchById(Long id);

    CartItemResponse update(Long id, CartItemUpdateRequest request);

    void delete(Long id);
}
