package Spring_AdamStore.mapper;

import Spring_AdamStore.constants.PaymentStatus;
import Spring_AdamStore.dto.response.AddressResponse;
import Spring_AdamStore.dto.response.OrderItemResponse;
import Spring_AdamStore.entity.OrderItem;
import Spring_AdamStore.entity.User;
import Spring_AdamStore.repository.AddressRepository;
import Spring_AdamStore.repository.OrderItemRepository;
import Spring_AdamStore.repository.PaymentHistoryRepository;
import Spring_AdamStore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OrderMappingHelper {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final AddressMappingHelper addressMappingHelper;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemMappingHelper orderItemMappingHelper;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public AddressResponse getAddressResponse(Long addressId){
        return addressRepository.findById(addressId)
                .map(address -> addressMapper.toAddressResponse(address, addressMappingHelper))
                .orElse(null);
    }

    public String getUserName(Long userId){
        return userRepository.findById(userId)
                .map(User::getName)
                .orElse(null);
    }

    public List<OrderItemResponse> getListOrderItemResponse(Long orderId){
        return orderItemMapper.toOrderItemResponseList(orderItemRepository.findAllByOrderId(orderId), orderItemMappingHelper);
    }

    public String getPaymentMethod(Long orderId){
        return paymentHistoryRepository.findByOrderIdAndPaymentStatus(orderId, PaymentStatus.PAID)
                .map(paymentHistory -> paymentHistory.getPaymentMethod().toString())
                .orElse(null);
    }


}
