package com.dev.backend.elasticsearch.repository;

import com.dev.backend.elasticsearch.document.EsProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EsProductRepository extends ElasticsearchRepository<EsProduct, String> {
    @Query("{\"bool\": { \"should\": [ " +
            "{\"match\": {\"name\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\", \"minimum_should_match\": \"30%\"}}}," +
            "{\"match\": {\"description\": {\"query\": \"?1\", \"fuzziness\": \"AUTO\", \"minimum_should_match\": \"30%\"}}}" +
            "]}}")
    List<EsProduct> findByNameOrDescriptionCustom(String name, String description);

    Page<EsProduct> findByNameOrDescription(String name, String description, Pageable page);
}
