package com.dev.backend.validator.handle;

import com.dev.backend.validator.FileSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeHandle implements ConstraintValidator<FileSize, MultipartFile> {
    private int maxSize;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        return value != null && value.getSize() <= maxSize;
    }
}
