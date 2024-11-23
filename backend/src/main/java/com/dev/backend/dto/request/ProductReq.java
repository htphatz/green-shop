package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductReq {
    private String name;
    private BigDecimal price;
    private MultipartFile fileImage;
    private String description;
    private Integer quantity;
    private String categoryId;
}
