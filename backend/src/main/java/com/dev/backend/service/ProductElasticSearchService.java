package com.dev.backend.service;

import com.dev.backend.document.ProductDocument;
import org.springframework.data.elasticsearch.core.SearchPage;

import java.math.BigDecimal;
import java.util.List;

public interface ProductElasticSearchService {
    SearchPage<ProductDocument> searchProducts(
            String keyword, String categoryId, BigDecimal minPrice, BigDecimal maxPrice,
            int page, int size, String sortBy, String sortDir);
    List<String> autocompleteSuggestions(String prefix);
}
