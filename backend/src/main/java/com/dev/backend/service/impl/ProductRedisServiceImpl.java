package com.dev.backend.service.impl;

import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.ProductRes;
import com.dev.backend.entity.Product;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.ProductMapper;
import com.dev.backend.repository.ProductRepository;
import com.dev.backend.service.BaseRedisService;
import com.dev.backend.service.ProductRedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductRedisServiceImpl implements ProductRedisService {
    private final ProductRepository productRepository;
    private final BaseRedisService<String, String, String> baseRedisService;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;

    @Override
    public ProductRes getProductRedisById(String id) throws JsonProcessingException {
        String key = String.format("product:%s", id);
        if (baseRedisService.get(key) == null) {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            ProductRes result = productMapper.toProductRes(product);
            String json = objectMapper.writeValueAsString(result);
            baseRedisService.set(key, json);
            baseRedisService.setTimeToLive(key, 43200L);
            return result;
        }
        else {
            String json = baseRedisService.get(key);
            return objectMapper.readValue(json, ProductRes.class);
        }
    }

    @Override
    public PageDto<ProductRes> searchProductsRedis(String keyword, String categoryId, Integer pageNumber, Integer pageSize) throws JsonProcessingException {
        String key = getKey(keyword, categoryId, pageNumber, pageSize);
        if (baseRedisService.get(key) == null) {
            pageNumber--;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Product> products = productRepository.searchProducts(keyword, categoryId, pageable);
            PageDto<ProductRes> result = PageDto.of(products).map(productMapper::toProductRes);
            String json = objectMapper.writeValueAsString(result);
            baseRedisService.set(key, json);
            baseRedisService.setTimeToLive(key, 43200L);
            return result;
        }
        else {
            String json = baseRedisService.get(key);
            return objectMapper.readValue(json, new TypeReference<PageDto<ProductRes>>() {});
        }
    }

    private String getKey(String keyword, String categoryId, Integer pageNumber, Integer pageSize) {
        return String.format("all_products:%s:%s:%s:%s", keyword, categoryId, pageNumber, pageSize);
    }
}
