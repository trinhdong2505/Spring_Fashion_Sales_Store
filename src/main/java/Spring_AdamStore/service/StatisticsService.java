package Spring_AdamStore.service;

import Spring_AdamStore.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {

    List<RevenueByMonthDTO> getRevenueByMonth(LocalDate startDate, LocalDate endDate);

    List<TopSellingDTO> getTopSellingProducts(LocalDate startDate, LocalDate endDate);

    List<OrderRevenueDTO> getOrderRevenueByDate(LocalDate startDate, LocalDate endDate);


}
