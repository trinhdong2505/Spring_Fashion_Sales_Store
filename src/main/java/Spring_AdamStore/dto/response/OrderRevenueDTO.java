package Spring_AdamStore.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRevenueDTO {

     private Long id;
     private String customerName;
     private Double totalPrice;
     private String paymentMethod;
     private LocalDate orderDate;
     private String orderStatus;

}
