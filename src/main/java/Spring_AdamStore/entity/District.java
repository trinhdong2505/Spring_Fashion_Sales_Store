package Spring_AdamStore.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Table(name = "districts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class District {

    @Id
    private Integer id;

    private String name;

    private Integer provinceId;
}
