package com.dev.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductReq {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Price is required")
    private BigDecimal price;

    private MultipartFile fileImage;
    private String description;

    @NotBlank(message = "Quantity is required")
    private Integer quantity;

    @NotBlank(message = "Category's ID is required")
    private String categoryId;
}
