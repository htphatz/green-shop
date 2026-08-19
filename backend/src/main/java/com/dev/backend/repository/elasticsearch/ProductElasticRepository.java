package com.dev.backend.repository.elasticsearch;

import com.dev.backend.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductElasticRepository extends ElasticsearchRepository<ProductDocument, String> {
}
