package Spring_AdamStore.entity;

import Spring_AdamStore.constants.PaymentMethod;
import Spring_AdamStore.constants.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "payment_history")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isPrimary;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime paymentTime;

     private Long orderId;



    @PrePersist
    public void handleBeforeCreate() {
        if(isPrimary == null){
            this.isPrimary = false;
        }
    }
}
