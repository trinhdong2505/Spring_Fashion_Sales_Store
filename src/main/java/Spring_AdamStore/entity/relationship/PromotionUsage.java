package Spring_AdamStore.entity.relationship;

import Spring_AdamStore.constants.EntityStatus;
import Spring_AdamStore.entity.Order;
import Spring_AdamStore.entity.Promotion;
import Spring_AdamStore.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "promotion_usages")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class PromotionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double discountAmount;
    private LocalDateTime usedAt;

    private Long userId;

    private Long promotionId;

    private Long orderId;


}
