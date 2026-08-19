package com.dev.backend.service.impl;

import com.dev.backend.document.ProductDocument;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductElasticSearchServiceImpl {
    private final ElasticsearchOperations elasticsearchOperations;

    /** Multi-field Full-Text Search with Relevancy Boosting, Fuzzy Tolerance, Filters & Dynamic Aggregations **/
    public SearchPage<ProductDocument> searchProducts(
            String keyword, String categoryId, BigDecimal minPrice, BigDecimal maxPrice,
            int page, int size, String sortBy, String sortDir) {

        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

        // 1. Full-Text Search with Relevancy Boosting & Typos Tolerance
        if (StringUtils.hasText(keyword)) {
            boolQueryBuilder.must(m -> m
                .multiMatch(mm -> mm
                    .query(keyword)
                    .fields("name^3", "categoryName^2", "description^1")
                    .fuzziness("AUTO")
                )
            );
        }

        // 2. Exact Filter by Category
        if (StringUtils.hasText(categoryId)) {
            boolQueryBuilder.filter(f -> f.term(t -> t.field("categoryId").value(categoryId)));
        }

        // 3. Numeric Range Filter for Price (using Elastic 8.x .number variant)
        if (minPrice != null || maxPrice != null) {
            boolQueryBuilder.filter(f -> f.range(r -> r
                .number(n -> {
                    n.field("price");
                    if (minPrice != null) n.gte(minPrice.doubleValue());
                    if (maxPrice != null) n.lte(maxPrice.doubleValue());
                    return n;
                })
            ));
        }

        queryBuilder.withQuery(boolQueryBuilder.build()._toQuery());

        // 4. Dynamic Aggregations for UI facets (Category distribution & Price statistics)
        queryBuilder.withAggregation("by_category", AggregationBuilders.terms().field("categoryId").size(10).build()._toAggregation());
        queryBuilder.withAggregation("price_stats", AggregationBuilders.stats().field("price").build()._toAggregation());

        // 5. Pagination & Sorting
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        queryBuilder.withPageable(PageRequest.of(page - 1, size, sort));

        SearchHits<ProductDocument> hits = elasticsearchOperations.search(queryBuilder.build(), ProductDocument.class);
        return SearchHitSupport.searchPageFor(hits, queryBuilder.getPageable());
    }

    /** Instant Autocomplete / Search-as-you-type using Edge N-Gram subfield **/
    public List<String> autocompleteSuggestions(String prefix) {
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(QueryBuilders.match(m -> m
                        .field("name.suggest")
                        .query(prefix)
                ))
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<ProductDocument> hits = elasticsearchOperations.search(query, ProductDocument.class);
        return hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getName())
                .distinct()
                .toList();
    }
}
