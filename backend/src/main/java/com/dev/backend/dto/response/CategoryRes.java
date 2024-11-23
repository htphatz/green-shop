package com.dev.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryRes {
    private String id;
    private String name;
    private String imageUrl;
}
