package Spring_AdamStore.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VariantRequest {

    @NotNull(message = "colorId không được null")
    private Long colorId;

    @NotNull(message = "sizeId không được null")
    private Long sizeId;

    @NotNull(message = "imageId không được null")
    private Long imageId;

    @NotNull(message = "Giá không được null")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private Double price;

    @NotNull(message = "Số lượng không được null")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer quantity;
}
