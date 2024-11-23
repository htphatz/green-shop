package com.dev.backend.mapper;

import com.dev.backend.dto.request.CategoryReq;
import com.dev.backend.dto.response.CategoryRes;
import com.dev.backend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "imageUrl", ignore = true)
    Category toCategory(CategoryReq request);

    CategoryRes toCategoryRes(Category category);
}
