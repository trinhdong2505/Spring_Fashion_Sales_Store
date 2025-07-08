package Spring_AdamStore.service.impl;

import Spring_AdamStore.dto.request.CartItemRequest;
import Spring_AdamStore.dto.request.CartItemUpdateRequest;
import Spring_AdamStore.dto.response.CartItemResponse;
import Spring_AdamStore.entity.*;
import Spring_AdamStore.exception.AppException;
import Spring_AdamStore.exception.ErrorCode;
import Spring_AdamStore.mapper.CartItemMapper;
import Spring_AdamStore.mapper.CartItemMappingHelper;
import Spring_AdamStore.repository.CartItemRepository;
import Spring_AdamStore.repository.CartRepository;
import Spring_AdamStore.repository.ProductVariantRepository;
import Spring_AdamStore.service.CartItemService;
import Spring_AdamStore.service.CurrentUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j(topic = "CART-ITEM-SERVICE")
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;
    private final CartItemMappingHelper cartItemMappingHelper;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public CartItemResponse create(CartItemRequest request) {
        log.info("Creating CartItem with data= {}", request);

        Cart cart = findCartByUser();

        ProductVariant productVariant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_EXISTED));

        // check sl hang con
        if (productVariant.getQuantity() < request.getQuantity()) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }

        Optional<CartItem> existingCartItem = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId());

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else{
            cartItem = CartItem.builder()
                    .quantity(request.getQuantity())
                    .price(productVariant.getPrice())
                    .productVariantId(productVariant.getId())
                    .cartId(cart.getId())
                    .build();
        }
        return cartItemMapper.toCartItemResponse(cartItemRepository.save(cartItem), cartItemMappingHelper);
    }

    @Override
    public CartItemResponse fetchById(Long id) {
        log.info("Fetching cart item by id: {}", id);

        CartItem cartItem = findCartItemById(id);
        return cartItemMapper.toCartItemResponse(cartItem, cartItemMappingHelper);
    }


    @Override
    @Transactional
    public CartItemResponse update(Long id, CartItemUpdateRequest request) {
        log.info("Updating cart item id: {} with data: {}", id, request);

        CartItem cartItem = findCartItemById(id);

        cartItem.setQuantity(request.getQuantity());
        return cartItemMapper.toCartItemResponse(cartItemRepository.save(cartItem), cartItemMappingHelper);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting cart item with id: {}", id);

        CartItem cartItem = findCartItemById(id);

        cartItemRepository.delete(cartItem);
    }

    private CartItem findCartItemById(Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_EXISTED));
    }

    private Cart findCartByUser(){
        User user = currentUserService.getCurrentUser();

        return cartRepository.findByUserId(user.getId())
                .orElseThrow(()->new AppException(ErrorCode.CART_NOT_EXISTED));
    }
}
