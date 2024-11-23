package com.dev.backend.service.impl;

import com.dev.backend.dto.request.CategoryReq;
import com.dev.backend.dto.response.CategoryRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.entity.Category;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.CategoryMapper;
import com.dev.backend.repository.CategoryRepository;
import com.dev.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final CategoryMapper categoryMapper;

    @Value("${resource.defaultImage}")
    private String defaultImage;

    @Override
    public CategoryRes createCategory(CategoryReq request) {
        Category category = categoryMapper.toCategory(request);
        String imageUrl = defaultImage;
        if (request.getFileImage() != null && !request.getFileImage().isEmpty()) {
            Map data = this.cloudinaryService.upload(request.getFileImage());
            imageUrl = (String) data.get("secure_url");
            category.setImageUrl(imageUrl);
        }
        return categoryMapper.toCategoryRes(categoryRepository.save(category));
    }

    @Override
    public CategoryRes getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toCategoryRes(category);
    }

    @Override
    public PageDto<CategoryRes> getAllCategories(Integer pageNumber, Integer pageSize) {
        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Category> categories = categoryRepository.findAll(pageable);
        return PageDto.of(categories).map(categoryMapper::toCategoryRes);
    }

    @Override
    @Transactional
    public CategoryRes updateCategory(String id, CategoryReq request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        category.setName(request.getName());
        if (request.getFileImage() != null && !request.getFileImage().isEmpty()) {
            Map data = this.cloudinaryService.upload(request.getFileImage());
            String newImageUrl = (String) data.get("secure_url");
            category.setImageUrl(newImageUrl);
        }
        return categoryMapper.toCategoryRes(categoryRepository.save(category));
    }
}
