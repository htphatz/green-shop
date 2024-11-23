package com.dev.backend.mapper;

import com.dev.backend.dto.request.ProductReq;
import com.dev.backend.dto.response.CategoryRes;
import com.dev.backend.dto.response.ProductRes;
import com.dev.backend.entity.Category;
import com.dev.backend.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mappings({
            @Mapping(target = "imageUrl", ignore = true),
            @Mapping(target = "category", ignore = true)
    })
    Product toProduct(ProductReq request);

    @Mapping(target = "category", source = "category")
    ProductRes toProductRes(Product product);

    CategoryRes toCategoryRes(Category category);
}
