package com.dev.backend.elasticsearch.service;

import com.dev.backend.elasticsearch.document.EsProduct;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EsProductService {
    Page<EsProduct> search(String keyword, Integer pageNumber, Integer pageSize);
    Page<EsProduct> search(String keyword, String categoryId, Integer pageNumber, Integer pageSize, Integer sort);
    Page<EsProduct> recommend(Long id, Integer pageNum, Integer pageSize);
}
