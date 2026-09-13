package com.dev.backend.controller;

import com.dev.backend.document.ProductDocument;
import com.dev.backend.dto.request.ProductReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.ProductRes;
import com.dev.backend.service.ProductElasticSearchService;
import com.dev.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
@Tag(name = "Product APIs")
public class ProductController {
    private final ProductService productService;
    private final ProductElasticSearchService productElasticSearchService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create product")
    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<ProductRes> createProduct(@Valid ProductReq request) {
        ProductRes result = productService.createProduct(request);
        return APIResponse.<ProductRes>builder().result(result).build();
    }

    @GetMapping
    @Operation(summary = "Search products")
    public APIResponse<PageDto<ProductRes>> searchProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) String categoryId,
            @RequestParam(name = "sortDir",defaultValue = "asc") String sortDir,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<ProductRes> result = productService.searchProducts(keyword, categoryId, sortDir, pageNumber, pageSize);
        return APIResponse.<PageDto<ProductRes>>builder().result(result).build();
    }

    @GetMapping("{id}")
    @Operation(summary = "Get product by id")
    public APIResponse<ProductRes> getProductById(@PathVariable("id") String id) {
        ProductRes result = productService.getProductById(id);
        return APIResponse.<ProductRes>builder().result(result).build();
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update product")
    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<ProductRes> updateCategory(@PathVariable("id") String id, @Valid ProductReq request) {
        ProductRes result = productService.updateProduct(id, request);
        return APIResponse.<ProductRes>builder().result(result).build();
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete product")
    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<Void> deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
        return APIResponse.<Void>builder().build();
    }

    @GetMapping(value = "search-by-custom-query")
    @Operation(summary = "Search products by custom query")
    public APIResponse<PageDto<ProductRes>> searchByCustomQuery(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<ProductRes> result = productService.searchByCustomQuery(pageNumber, pageSize, sortBy, keyword);
        return APIResponse.<PageDto<ProductRes>>builder().result(result).build();
    }

    @GetMapping(value = "search-by-criteria")
    @Operation(summary = "Search products by criteria")
    public APIResponse<PageDto<ProductRes>> searchByCriteria(
            @RequestParam(name = "keyword", required = false) String[] search,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<ProductRes> result = productService.searchByCriteria(pageNumber, pageSize, sortBy, category, search);
        return APIResponse.<PageDto<ProductRes>>builder().result(result).build();
    }

    @GetMapping("search-es")
    @Operation(summary = "Full-text search products via Elasticsearch")
    public APIResponse<SearchPage<ProductDocument>> searchProductsES(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) String categoryId,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "name.keyword") String sortBy,
            @RequestParam(name = "sortDir", required = false, defaultValue = "asc") String sortDir
    ) {
        SearchPage<ProductDocument> result = productElasticSearchService.searchProducts(keyword, categoryId, minPrice, maxPrice, page, size, sortBy, sortDir);
        return APIResponse.<SearchPage<ProductDocument>>builder().result(result).build();
    }

    @GetMapping("autocomplete")
    @Operation(summary = "Product autocomplete suggestions")
    public APIResponse<List<String>> autocompleteSuggestions(@RequestParam("prefix") String prefix) {
        List<String> result = productElasticSearchService.autocompleteSuggestions(prefix);
        return APIResponse.<List<String>>builder().result(result).build();
    }
}
