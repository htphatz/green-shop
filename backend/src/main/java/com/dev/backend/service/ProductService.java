package com.dev.backend.service;

import com.dev.backend.dto.request.ProductReq;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.ProductRes;

public interface ProductService {
    ProductRes createProduct(ProductReq request);
    ProductRes getProductById(String id);
    ProductRes updateProduct(String id, ProductReq request);
    PageDto<ProductRes> searchProducts(String keyword, String categoryId, Integer pageNumber, Integer pageSize);
    void deleteProduct(String id);
}
