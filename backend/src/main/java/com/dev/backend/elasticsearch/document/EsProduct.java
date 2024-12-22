package com.dev.backend.elasticsearch.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Document(indexName = "products")
public class EsProduct implements Serializable {
    @Serial
    private static final long serialVersionUID = -1L;

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    private BigDecimal price;
    private String imageUrl;

    @Field(type = FieldType.Text)
    private String description;

    private Integer quantity;
    private Integer soldQuantity;

    @Field(type = FieldType.Text)
    private String categoryId;
}
