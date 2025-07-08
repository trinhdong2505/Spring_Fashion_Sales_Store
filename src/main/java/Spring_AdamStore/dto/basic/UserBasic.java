package Spring_AdamStore.dto.basic;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBasic {

    private Long id;
    private String name;
    private String email;
    private String phone;
}
