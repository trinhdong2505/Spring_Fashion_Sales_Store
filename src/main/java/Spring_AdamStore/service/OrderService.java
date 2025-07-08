package Spring_AdamStore.service;

import Spring_AdamStore.dto.request.OrderRequest;
import Spring_AdamStore.dto.request.OrderAddressRequest;
import Spring_AdamStore.dto.response.OrderResponse;
import Spring_AdamStore.dto.response.PageResponse;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse create(OrderRequest request);

    OrderResponse fetchDetailById(Long id);

    PageResponse<OrderResponse> fetchAll(Pageable pageable);

    OrderResponse updateAddress(Long id, OrderAddressRequest request);

    void delete(Long id);

    PageResponse<OrderResponse> searchOrder(Pageable pageable, List<String> search);

    OrderResponse cancelOrder(Long orderId);
}
