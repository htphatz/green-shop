package com.dev.backend.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteriaConsumer implements Consumer<SearchCriteria> {
    private CriteriaBuilder builder;
    private Predicate predicate;
    private Root<?> root;

    @Override
    public void accept(SearchCriteria param) {
        String key = param.getKey();
        String op = param.getOperation();
        String valStr = param.getValue() != null ? param.getValue().toString() : "";

        Class<?> fieldType = root.get(key).getJavaType();

        if (">".equals(op)) {
            if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
                predicate = builder.and(predicate, builder.ge(root.get(key), new java.math.BigDecimal(valStr)));
            } else {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(key), valStr));
            }
        } else if ("<".equals(op)) {
            if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
                predicate = builder.and(predicate, builder.le(root.get(key), new java.math.BigDecimal(valStr)));
            } else {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get(key), valStr));
            }
        } else {
            if (fieldType == String.class) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get(key)), "%" + valStr.toLowerCase() + "%"));
            } else {
                predicate = builder.and(predicate, builder.equal(root.get(key), param.getValue()));
            }
        }
    }
}
