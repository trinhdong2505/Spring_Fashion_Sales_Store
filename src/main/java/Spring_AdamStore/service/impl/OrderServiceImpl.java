package Spring_AdamStore.service.impl;

import Spring_AdamStore.constants.OrderStatus;
import Spring_AdamStore.constants.PaymentMethod;
import Spring_AdamStore.constants.RoleEnum;
import Spring_AdamStore.dto.request.*;
import Spring_AdamStore.dto.response.*;
import Spring_AdamStore.entity.*;
import Spring_AdamStore.entity.Order;
import Spring_AdamStore.entity.relationship.PromotionUsage;
import Spring_AdamStore.exception.AppException;
import Spring_AdamStore.exception.ErrorCode;
import Spring_AdamStore.mapper.OrderMapper;
import Spring_AdamStore.mapper.OrderMappingHelper;
import Spring_AdamStore.repository.*;
import Spring_AdamStore.repository.criteria.SearchCriteriaQueryConsumer;
import Spring_AdamStore.repository.criteria.SearchCriteria;
import Spring_AdamStore.repository.relationship.PromotionUsageRepository;
import Spring_AdamStore.service.CurrentUserService;
import Spring_AdamStore.service.OrderService;
import Spring_AdamStore.service.PaymentHistoryService;
import Spring_AdamStore.service.relationship.PromotionUsageService;
import Spring_AdamStore.service.relationship.UserHasRoleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "ORDER-SERVICE")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final CurrentUserService currentUserService;
    private final AddressRepository addressRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderItemRepository orderItemRepository;
    private final PromotionRepository promotionRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserHasRoleService userHasRoleService;
    private final PromotionUsageService promotionUsageService;
    private final PromotionUsageRepository promotionUsageRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final OrderMappingHelper orderMappingHelper;
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        log.info("Creating order with data= {}", request);

        // ShippingFee
        double totalPrice = request.getShippingFee();

        OrderStatus orderStatus = request.getPaymentMethod() == PaymentMethod.CASH
                ? OrderStatus.PROCESSING
                : OrderStatus.PENDING;

        Order order = Order.builder()
                .userId(currentUserService.getCurrentUser().getId())
                .addressId(findAddressById(request.getAddressId()).getId())
                .orderStatus(orderStatus)
                .totalPrice(totalPrice)
                .orderDate(LocalDate.now())
                .build();

        orderRepository.save(order);

        // orderItem
        Set<OrderItem> orderItemSet = new HashSet<>();
        for(OrderItemRequest orderItemRequest : request.getOrderItems()){
            ProductVariant productVariant = productVariantRepository.findById(orderItemRequest.getProductVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_EXISTED));

            // check sl hang con
            if (productVariant.getQuantity() < orderItemRequest.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            OrderItem orderItem = OrderItem.builder()
                    .quantity(orderItemRequest.getQuantity())
                    .unitPrice(productVariant.getPrice())
                    .orderId(order.getId())
                    .productVariantId(productVariant.getId())
                    .build();

            // tru so luong hang trong kho
            productVariant.setQuantity(productVariant.getQuantity() - orderItemRequest.getQuantity());
            productVariantRepository.save(productVariant);

            orderItemSet.add(orderItem);
            totalPrice += orderItem.getQuantity() * orderItem.getUnitPrice();
        }
        orderItemRepository.saveAll(orderItemSet);

        // Promotion
        if (request.getPromotionId() != null) {
            totalPrice = applyPromotion(request.getPromotionId(), order, totalPrice);
        }

        order.setTotalPrice(totalPrice);

        // PaymentHistory
        paymentHistoryService.savePaymentHistory(order, request.getPaymentMethod());

        return orderMapper.toOrderResponse(orderRepository.save(order), orderMappingHelper);
    }


    @Override
    public OrderResponse fetchDetailById(Long id) {
        log.info("Fetch Order By Id: {}", id);

        Order order = findOrderById(id);

        return orderMapper.toOrderResponse(order, orderMappingHelper);
    }

    @Override
    public PageResponse<OrderResponse> fetchAll(Pageable pageable) {
        log.info("Fetch All Order For Admin");

        Page<Order> orderPage = orderRepository.findAll(pageable);

        return PageResponse.<OrderResponse>builder()
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalItems(orderPage.getTotalElements())
                .items(orderMapper.toOrderResponseList(orderPage.getContent(), orderMappingHelper))
                .build();
    }

    @Override
    public PageResponse<OrderResponse> searchOrder(Pageable pageable, List<String> search) {
       List<SearchCriteria> criteriaList = new ArrayList<>();

        // Lay danh sach dieu kien
        if(search != null){
            for(String s : search){
                Pattern pattern = Pattern.compile("(\\w+?)(~|>|<)(.*)");
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()){
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }
        List<Order> orderList = getOrderList(pageable, criteriaList);

        Long totalElements = getTotalElements(criteriaList);
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        return PageResponse.<OrderResponse>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPages(totalPages)
                .totalItems(totalElements)
                .items(orderMapper.toOrderResponseList(orderList, orderMappingHelper))
                .build();
    }


    private List<Order> getOrderList(Pageable pageable, List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = builder.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);

        // xu ly dieu kien
        Predicate predicate = builder.conjunction();

        // check user
        User currentUser = currentUserService.getCurrentUser();
        boolean isAdmin = userHasRoleService.checkRoleForUser(currentUser, RoleEnum.ADMIN);
        if (!isAdmin) {
            Join<Order, User> userJoin = root.join("user", JoinType.LEFT);
            predicate = builder.and(predicate, builder.equal(userJoin.get("id"), currentUser.getId()));
        }

        // search
        if(!CollectionUtils.isEmpty(criteriaList)){
            SearchCriteriaQueryConsumer queryConsumer = new SearchCriteriaQueryConsumer(builder, predicate, root);
            criteriaList.forEach(queryConsumer);

            predicate = builder.and(predicate, queryConsumer.getPredicate());
        }

        query.where(predicate);

        // Sort
        List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();

            Path<?> path = root.get(property);
            orders.add(order.isAscending() ? builder.asc(path) : builder.desc(path));
        }

        query.orderBy(orders);

        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    private Long getTotalElements(List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Order> root = countQuery.from(Order.class);

        // Xu ly dieu kien tim kiem
        Predicate predicate = builder.conjunction();

        // check user
        User currentUser = currentUserService.getCurrentUser();
        boolean isAdmin = userHasRoleService.checkRoleForUser(currentUser, RoleEnum.ADMIN);
        if (!isAdmin) {
            Join<Order, User> userJoin = root.join("user", JoinType.LEFT);
            predicate = builder.and(predicate, builder.equal(userJoin.get("id"), currentUser.getId()));
        }

        // search
        if(!CollectionUtils.isEmpty(criteriaList)){
            SearchCriteriaQueryConsumer queryConsumer = new SearchCriteriaQueryConsumer(builder, predicate, root);
            criteriaList.forEach(queryConsumer);

            predicate = builder.and(predicate, queryConsumer.getPredicate());
        }

        countQuery.select(builder.count(root));
        countQuery.where(predicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    @Override
    @Transactional
    public OrderResponse updateAddress(Long id, OrderAddressRequest request) {
        log.info("Updated update address for orderId= {}" , id);

        Order order = findOrderById(id);

        if (!(order.getOrderStatus() == OrderStatus.PENDING || order.getOrderStatus() == OrderStatus.PROCESSING)) {
            throw new AppException(ErrorCode.ORDER_CANNOT_UPDATE_ADDRESS);
        }

        Address newAddress = findAddressById(request.getAddressId());


        order.setAddressId(newAddress.getId());
        return orderMapper.toOrderResponse(orderRepository.save(order), orderMappingHelper);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Delete Order By Id: {}", id);

        Order order = findOrderById(id);

        orderItemRepository.deleteAllByOrderId(order.getId());

        paymentHistoryRepository.deleteAllByOrderId(order.getId());

        promotionUsageRepository.deleteAllByOrderId(order.getId());

        orderRepository.delete(order);
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        Order order = findOrderById(orderId);

        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.PROCESSING) {
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        return orderMapper.toOrderResponse(orderRepository.save(order), orderMappingHelper);
    }


    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
    }

    private Address findAddressById(Long addressId){
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXISTED));
    }

    private double applyPromotion(Long promotionId, Order order, double currentTotal) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_EXISTED));

        PromotionUsage usage = promotionUsageService.applyPromotion(promotion, order, currentUserService.getCurrentUser(), currentTotal);

        log.info("Usage Discount Amount: {}", usage.getDiscountAmount());
        log.info("Discount : {}", currentTotal * usage.getDiscountAmount());
        return currentTotal - usage.getDiscountAmount();
    }





}
