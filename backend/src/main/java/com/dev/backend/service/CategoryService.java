package com.dev.backend.service;

import com.dev.backend.dto.request.CategoryReq;
import com.dev.backend.dto.response.CategoryRes;
import com.dev.backend.dto.response.PageDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface CategoryService {
    CategoryRes createCategory(CategoryReq request);
    CategoryRes getCategoryById(String id) throws JsonProcessingException;
    PageDto<CategoryRes> getAllCategories(Integer pageNumber, Integer pageSize) throws JsonProcessingException;
    CategoryRes updateCategory(String id, CategoryReq request);
}
