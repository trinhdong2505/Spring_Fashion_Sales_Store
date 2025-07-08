package Spring_AdamStore.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class PaymentCallbackRequest {

    @NotBlank(message = "Response Code không được để trống")
    private String responseCode;

    @Min(value = 1, message = "orderId phải lớn hơn 0")
    @NotNull(message = "orderId không được null")
    private Long orderId;
}
