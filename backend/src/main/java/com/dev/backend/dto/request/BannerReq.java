package com.dev.backend.dto.request;

import com.dev.backend.validator.FileNotEmpty;
import com.dev.backend.validator.FileSize;
import com.dev.backend.validator.FileType;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class BannerReq {
    @NotBlank(message = "Name is required")
    private String name;

    @FileNotEmpty
    @FileSize(maxSize = 2000000) //bytes
    @FileType(contentType = {"image/png", "image/jpeg", "image/jpg"})
    private MultipartFile fileImage;
}
