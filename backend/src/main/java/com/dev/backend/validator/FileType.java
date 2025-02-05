package com.dev.backend.validator;

import com.dev.backend.validator.handle.FileTypeHandle;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { FileTypeHandle.class })
@Target({ FIELD })
@Retention(RUNTIME)
public @interface FileType {
    String message() default "File type isn't supported";

    String[] contentType();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
