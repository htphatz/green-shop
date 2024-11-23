package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class CategoryReq {
    private String name;
    private MultipartFile fileImage;
}
