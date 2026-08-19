package com.dev.backend.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
@Setting(settingPath = "es-config/product-analyzer.json")
public class ProductDocument {

    @Id
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer", searchAnalyzer = "vietnamese_analyzer"),
            otherFields = {
                    @InnerField(suffix = "suggest", type = FieldType.Text, analyzer = "autocomplete_analyzer", searchAnalyzer = "autocomplete_search_analyzer"),
                    @InnerField(suffix = "raw", type = FieldType.Keyword)
            }
    )
    private String name;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String description;

    @Field(type = FieldType.Integer)
    private Integer quantity;

    @Field(type = FieldType.Integer)
    private Integer soldQuantity;

    @Field(type = FieldType.Keyword)
    private String categoryId;

    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String categoryName;

    @Field(type = FieldType.Keyword)
    private String imageUrl;
}
