package com.dev.backend.elasticsearch.service.impl;

import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.dev.backend.elasticsearch.document.EsProduct;
import com.dev.backend.elasticsearch.repository.EsProductRepository;
import com.dev.backend.elasticsearch.service.EsProductService;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.client.elc.Queries;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.SearchTemplateQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EsProductServiceImpl implements EsProductService {
    private final EsProductRepository esProductRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<EsProduct> search(String keyword, Integer pageNumber, Integer pageSize) {
        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return esProductRepository.findByNameOrDescription(keyword, keyword, pageable);
    }

    @Override
    public Page<EsProduct> search(String keyword, String categoryId, Integer pageNumber, Integer pageSize, Integer sort) {
        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Tạo Native Query
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        // Phân trang
        nativeQueryBuilder.withPageable(pageable);

        if (categoryId != null) {
            // Tạo Term Query
            TermQuery termQuery = Queries.termQuery("categoryId", categoryId);
            nativeQueryBuilder.withFilter(termQuery._toQuery());
        }

        if (StringUtil.isNullOrEmpty(keyword)) {
            // Nếu không có keyword thì sử dụng matchAllQuery()
            nativeQueryBuilder.withQuery(Queries.matchAllQuery()._toQuery());
        } else {
            // Tạo Multi Match Query cho "name", "description"
            MultiMatchQuery multiMatchQuery =
                    MultiMatchQuery.of(
                            builder -> builder
                                .query(keyword)
                                .fields(List.of("name", "description"))
                                .boost(1F)
                    );
            nativeQueryBuilder.withQuery(multiMatchQuery._toQuery());
        }

        if (sort == 1) {
            // Sắp xếp giảm dần giá sản phẩm
            nativeQueryBuilder.withSort(SortOptionsBuilders
                    .field(builder -> builder
                            .field("price")
                            .order(SortOrder.Desc)));
        } else {
            // Sắp xếp tăng dần giá sản phẩm
            nativeQueryBuilder.withSort(SortOptionsBuilders
                    .field(builder -> builder
                            .field("price")
                            .order(SortOrder.Asc)));
        }

        // Chuyển Native Query sang Search Query
        SearchTemplateQuery searchQuery = SearchTemplateQuery.builder()
                .withHighlightQuery(nativeQueryBuilder.getHighlightQuery())
                .withSourceFilter(nativeQueryBuilder.getSourceFilter())
                .withSort(nativeQueryBuilder.getSort())
                .withPageable(nativeQueryBuilder.getPageable())
                .build();

        SearchHits<EsProduct> searchHits = elasticsearchOperations.search(searchQuery, EsProduct.class);
        if (searchHits.getTotalHits() <= 0) {
            return Page.empty();
        }
        List<EsProduct> searchEsProducts = searchHits.stream().map(SearchHit::getContent).toList();
        return new PageImpl<>(searchEsProducts, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<EsProduct> recommend(Long id, Integer pageNum, Integer pageSize) {
        return null;
    }
}
