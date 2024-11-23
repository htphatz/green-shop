package com.dev.backend.controller;

import com.dev.backend.dto.request.CategoryReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.CategoryRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<CategoryRes> createCategory(CategoryReq request) {
        CategoryRes result = categoryService.createCategory(request);
        return APIResponse.<CategoryRes>builder().result(result).build();
    }

    @GetMapping
    public APIResponse<PageDto<CategoryRes>> getCategories(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<CategoryRes> result = categoryService.getAllCategories(pageNumber, pageSize);
        return APIResponse.<PageDto<CategoryRes>>builder().result(result).build();
    }

    @GetMapping("/{id}")
    public APIResponse<CategoryRes> getCategoryById(@PathVariable("id") String id) {
        CategoryRes result = categoryService.getCategoryById(id);
        return APIResponse.<CategoryRes>builder().result(result).build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<CategoryRes> updateCategory(@PathVariable("id") String id, CategoryReq request) {
        CategoryRes result = categoryService.updateCategory(id, request);
        return APIResponse.<CategoryRes>builder().result(result).build();
    }
}
