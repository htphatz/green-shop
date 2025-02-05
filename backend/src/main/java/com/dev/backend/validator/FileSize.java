package com.dev.backend.validator;

import com.dev.backend.validator.handle.FileSizeHandle;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { FileSizeHandle.class })
@Target({ FIELD })
@Retention(RUNTIME)
public @interface FileSize {
    String message() default "File too large";

    int maxSize();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
