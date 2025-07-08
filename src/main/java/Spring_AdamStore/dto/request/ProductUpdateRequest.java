package Spring_AdamStore.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class ProductUpdateRequest {

    private String name;

    private String description;

    private Long categoryId;

    @NotEmpty(message = "Danh sách biến thể sản phẩm không được để trống")
    @Size(min = 1, message = "Phải có ít nhất một biến thể sản phẩm")
    private List<VariantRequest> variants;

}
