package com.dev.backend.dto.request;

import com.dev.backend.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class VoucherReq {
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Discount type is required")
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @NotBlank(message = "Discount value is required")
    @Min(value = 1, message = "Discount value must be >= 0")
    private Double discountValue;

    @NotBlank(message = "Expiration date is required")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expirationDate;
}
