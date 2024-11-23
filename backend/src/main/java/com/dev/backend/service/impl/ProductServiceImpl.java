package com.dev.backend.service.impl;

import com.dev.backend.dto.request.ProductReq;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.ProductRes;
import com.dev.backend.entity.Category;
import com.dev.backend.entity.Product;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.ProductMapper;
import com.dev.backend.repository.CategoryRepository;
import com.dev.backend.repository.ProductRepository;
import com.dev.backend.service.ProductService;
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
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductMapper productMapper;

    @Value("${resource.defaultImage}")
    private String defaultImage;

    @Override
    public ProductRes createProduct(ProductReq request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Product product = productMapper.toProduct(request);
        product.setCategory(category);
        product.setSoldQuantity(0);
        String imageUrl = defaultImage;
        if (request.getFileImage() != null && !request.getFileImage().isEmpty()) {
            Map data = this.cloudinaryService.upload(request.getFileImage());
            imageUrl = (String) data.get("secure_url");
            product.setImageUrl(imageUrl);
        }
        return productMapper.toProductRes(productRepository.save(product));
    }

    @Override
    public ProductRes getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductRes(product);
    }

    @Override
    @Transactional
    public ProductRes updateProduct(String id, ProductReq request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setName(request.getName());
            product.setPrice(request.getPrice());
            product.setDescription(request.getDescription());
            if (request.getQuantity() < product.getSoldQuantity()) {
                throw new AppException(ErrorCode.QUANTITY_INVALID);
            }
            product.setQuantity(request.getQuantity());
            product.setCategory(category);
            if (request.getFileImage() != null && !request.getFileImage().isEmpty()) {
                Map data = this.cloudinaryService.upload(request.getFileImage());
                String newImageUrl = (String) data.get("secure_url");
                product.setImageUrl(newImageUrl);
            }
            return productMapper.toProductRes(productRepository.save(product));
        }
        return null;
    }

    @Override
    public PageDto<ProductRes> searchProducts(String keyword, String categoryId, Integer pageNumber, Integer pageSize) {
        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> products = productRepository.searchProducts(keyword, categoryId, pageable);
        return PageDto.of(products).map(productMapper::toProductRes);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
