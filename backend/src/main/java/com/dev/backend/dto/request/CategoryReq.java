package com.dev.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class CategoryReq {
    @NotBlank(message = "Name is required")
    private String name;

    private MultipartFile fileImage;
}
