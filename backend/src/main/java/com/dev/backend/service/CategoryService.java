package com.dev.backend.service;

import com.dev.backend.dto.request.CategoryReq;
import com.dev.backend.dto.response.CategoryRes;
import com.dev.backend.dto.response.PageDto;

public interface CategoryService {
    CategoryRes createCategory(CategoryReq request);
    CategoryRes getCategoryById(String id);
    PageDto<CategoryRes> getAllCategories(Integer pageNumber, Integer pageSize);
    CategoryRes updateCategory(String id, CategoryReq request);
}
