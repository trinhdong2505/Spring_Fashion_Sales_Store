package Spring_AdamStore.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Table(name = "wards")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Ward {

    @Id
    private String code;

    private String name;

    private Integer districtId;
}
