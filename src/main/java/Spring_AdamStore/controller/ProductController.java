package Spring_AdamStore.controller;

import Spring_AdamStore.dto.request.ProductRequest;
import Spring_AdamStore.dto.request.ProductUpdateRequest;
import Spring_AdamStore.dto.response.*;
import Spring_AdamStore.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "PRODUCT-CONTROLLER")
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
@RestController
public class ProductController {

    private final ProductService productService;


    @PostMapping("/admin/products")
    public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductRequest request){
        log.info("Received request to create product: {}", request);

        return ApiResponse.<ProductResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create Product")
                .result(productService.create(request))
                .build();
    }


    @Operation(summary = "Fetch Product Detail By Id",
            description = "Api này để lấy chi tiết của sản phẩm theo Id")
    @GetMapping("/public/products/{id}/details")
    public ApiResponse<ProductResponse> fetchDetailById(@Min(value = 1, message = "ID phải lớn hơn 0")
                                               @PathVariable Long id){
        log.info("Received request to Fetch Product Detail by id: {}", id);

        return ApiResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Fetch Product By Id")
                .result(productService.fetchDetailById(id))
                .build();
    }

    @Operation(summary = "Fetch All Products For User",
    description = "Api này để lấy các Products (ACTIVE) cho user")
    @GetMapping("/public/products")
    public ApiResponse<PageResponse<ProductResponse>> fetchAll(@ParameterObject @PageableDefault Pageable pageable){
        log.info("Received request to fetch all products for User");

        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(productService.fetchAll(pageable))
                .message("Fetch All Products For User")
                .build();
    }


    @Operation(summary = "Fetch All Products For Admin",
            description = "Api này để lấy các Products (cả ACTIVE và INACTIVE) cho admin")
    @GetMapping("/admin/products")
    public ApiResponse<PageResponse<ProductResponse>> fetchAllProductsForAdmin(@ParameterObject @PageableDefault Pageable pageable){
        log.info("Received request to fetch all products for Admin");

        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(productService.fetchAllProductsForAdmin(pageable))
                .message("Fetch All Products For Admin")
                .build();
    }



    @PutMapping("/admin/products/{id}")
    public ApiResponse<ProductResponse> update(@Min(value = 1, message = "ID phải lớn hơn 0")
                                            @PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request){
        return ApiResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Update Product By Id")
                .result(productService.update(id, request))
                .build();
    }



    @Operation(summary = "Soft Delete Product")
    @DeleteMapping("/admin/products/{id}")
    public ApiResponse<Void> delete(@Min(value = 1, message = "ID phải lớn hơn 0")
                                    @PathVariable Long id){
        productService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Soft Delete Product By Id")
                .result(null)
                .build();
    }


    @Operation(summary = "Restore Product")
    @PatchMapping("/admin/products/{id}/restore")
    public ApiResponse<ProductResponse> restore(@Min(value = 1, message = "Id phải lớn hơn 0")
                                               @PathVariable long id) {
        return ApiResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Restore Product By Id")
                .result(productService.restore(id))
                .build();
    }

    @Operation(description = "Api này dùng để search product, giá trị của search: field~value hoặc field>value hoặc field<value")
    @GetMapping("/public/products/search")
    public ApiResponse<PageResponse<ProductResponse>> searchProduct(@ParameterObject @PageableDefault Pageable pageable,
                                                                    @RequestParam(required = false) List<String> search){
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(productService.searchProduct(pageable, search))
                .message("Search Products based on attributes with pagination")
                .build();
    }

    @GetMapping("/public/products/{productId}/reviews")
    public ApiResponse<PageResponse<ReviewResponse>> fetchReviewsByProductId(@ParameterObject @PageableDefault Pageable pageable,
                                                                             @Min(value = 1, message = "ID phải lớn hơn 0")
                                                                         @PathVariable Long productId) {
        return ApiResponse.<PageResponse<ReviewResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Fetch Reviews By Product Id")
                .result(productService.fetchReviewsByProductId(pageable, productId))
                .build();
    }



    @Operation(summary = "Fetch All product-variants by product for Admin",
            description = "Api này dùng để lấy tất ca Product-Variants (cả ACTIVE và INACTIVE) theo Product cho admin")
    @GetMapping("/admin/products/{productId}/product-variants/admin")
    public ApiResponse<PageResponse<ProductVariantResponse>> getVariantsByProductIdForAdmin(@ParameterObject @PageableDefault Pageable pageable,
                                                                                    @Min(value = 1, message = "ID phải lớn hơn 0")
                                                                                    @PathVariable Long productId){
        return ApiResponse.<PageResponse<ProductVariantResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Fetch All product-variants by product for Admin")
                .result(productService.getVariantsByProductIdForAdmin(pageable, productId))
                .build();
    }
}
