package com.dev.backend.controller;

import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.ProductRes;
import com.dev.backend.service.ProductRedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("redis/products")
@RequiredArgsConstructor
public class ProductRedisController {
    private final ProductRedisService productRedisService;

    @GetMapping("{id}")
    public APIResponse<ProductRes> getProductById(@PathVariable("id") String id) throws JsonProcessingException {
        ProductRes result = productRedisService.getProductRedisById(id);
        return APIResponse.<ProductRes>builder().result(result).build();
    }

    @GetMapping
    public APIResponse<PageDto<ProductRes>> searchProductsRedis(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) String categoryId,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) throws JsonProcessingException {
        PageDto<ProductRes> result = productRedisService.searchProductsRedis(keyword, categoryId, pageNumber, pageSize);
        return APIResponse.<PageDto<ProductRes>>builder().result(result).build();
    }
}
