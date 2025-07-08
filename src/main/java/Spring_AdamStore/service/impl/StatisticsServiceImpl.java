package Spring_AdamStore.service.impl;

import Spring_AdamStore.constants.OrderStatus;
import Spring_AdamStore.dto.response.*;
import Spring_AdamStore.entity.Order;
import Spring_AdamStore.mapper.OrderMapper;
import Spring_AdamStore.mapper.OrderMappingHelper;
import Spring_AdamStore.repository.OrderRepository;
import Spring_AdamStore.repository.PaymentHistoryRepository;
import Spring_AdamStore.repository.ProductRepository;
import Spring_AdamStore.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j(topic = "STATISTICS-SERVICE")
@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderMappingHelper orderMappingHelper;

    @Override
    public List<RevenueByMonthDTO> getRevenueByMonth(LocalDate startDate, LocalDate endDate) {
        log.info("Getting monthly revenue from {} to {}", startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return paymentHistoryRepository.getRevenueByMonth(startDateTime, endDateTime);
    }

    @Override
    public List<TopSellingDTO> getTopSellingProducts(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching top-selling products from {} to {}", startDate, endDate);

        return productRepository.findTopSellingProductsBetween(startDate, endDate);
    }

    @Override
    public List<OrderRevenueDTO> getOrderRevenueByDate(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching daily order revenue from {} to {}", startDate, endDate);

        List<Order> orderList = orderRepository.findAllByOrderDateBetweenAndOrderStatus(
                startDate, endDate, OrderStatus.DELIVERED);

        return orderMapper.tOrderRevenueDtoList(orderList, orderMappingHelper);
    }

}
