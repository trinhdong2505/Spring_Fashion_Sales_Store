package Spring_AdamStore.dto.basic;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderBasic {

    private Long id;
    private LocalDate orderDate;

    private String username;
    private String email;
}
