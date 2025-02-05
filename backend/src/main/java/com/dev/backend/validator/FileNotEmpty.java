package com.dev.backend.validator;

import com.dev.backend.validator.handle.FileNotEmptyHandle;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { FileNotEmptyHandle.class })
@Target({ FIELD })
@Retention(RUNTIME)
public @interface FileNotEmpty {
    String message() default "File must be not empty";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
