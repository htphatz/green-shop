package com.dev.backend.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    // price, description,...
    private String key;

    // >, :, <, >=, <=
    private String operation;

    private Object value;
}
