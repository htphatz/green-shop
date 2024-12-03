package com.dev.backend.service;

import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.ProductRes;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ProductRedisService {
    ProductRes getProductRedisById(String id) throws JsonProcessingException;
    PageDto<ProductRes> searchProductsRedis(String keyword, String categoryId, Integer pageNumber, Integer pageSize) throws JsonProcessingException;
}
