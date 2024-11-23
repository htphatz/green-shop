package com.dev.backend.controller;

import com.dev.backend.dto.request.ProductReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.ProductRes;
import com.dev.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<ProductRes> createProduct(ProductReq request) {
        ProductRes result = productService.createProduct(request);
        return APIResponse.<ProductRes>builder().result(result).build();
    }

    @GetMapping
    public APIResponse<PageDto<ProductRes>> searchProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) String categoryId,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<ProductRes> result = productService.searchProducts(keyword, categoryId, pageNumber, pageSize);
        return APIResponse.<PageDto<ProductRes>>builder().result(result).build();
    }

    @GetMapping("{id}")
    public APIResponse<ProductRes> getProductById(@PathVariable("id") String id) {
        ProductRes result = productService.getProductById(id);
        return APIResponse.<ProductRes>builder().result(result).build();
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<ProductRes> updateCategory(@PathVariable("id") String id, ProductReq request) {
        ProductRes result = productService.updateProduct(id, request);
        return APIResponse.<ProductRes>builder().result(result).build();
    }

    @DeleteMapping(value = "/{id}")
    public APIResponse<Void> deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
        return APIResponse.<Void>builder().build();
    }
}
