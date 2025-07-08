package Spring_AdamStore.dto.response;

import Spring_AdamStore.constants.EntityStatus;
import Spring_AdamStore.dto.basic.EntityBasic;
import Spring_AdamStore.dto.basic.UserBasic;
import Spring_AdamStore.dto.basic.WardBasic;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long id;

    private Boolean isDefault;

    private Boolean isVisible;

    private EntityStatus status;

    private String phone;
    private String streetDetail;

    private WardBasic ward;

    private EntityBasic district;

    private EntityBasic province;


}
