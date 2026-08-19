package com.dev.backend.scheduler;

import com.dev.backend.document.ProductDocument;
import com.dev.backend.entity.Product;
import com.dev.backend.repository.ProductRepository;
import com.dev.backend.repository.elasticsearch.ProductElasticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductReindexScheduler {

    private final ProductRepository productRepository;
    private final ProductElasticRepository productElasticRepository;

    /** Nightly scheduled batch job to reconcile data between MySQL and Elasticsearch in chunks to prevent OOM **/
    @Scheduled(cron = "0 0 2 * * ?")
    public void reindexAllProducts() {
        log.info("Starting Full Product Re-indexing job...");
        int pageSize = 500;
        int pageNumber = 0;
        Page<Product> productPage;

        do {
            productPage = productRepository.findAll(PageRequest.of(pageNumber, pageSize));
            List<ProductDocument> docs = productPage.getContent().stream()
                    .map(p -> ProductDocument.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .price(p.getPrice())
                            .description(p.getDescription())
                            .quantity(p.getQuantity())
                            .soldQuantity(p.getSoldQuantity())
                            .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                            .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                            .imageUrl(p.getImageUrl())
                            .build())
                    .toList();

            productElasticRepository.saveAll(docs);
            pageNumber++;
            log.info("Re-indexed batch page {}", pageNumber);
        } while (productPage.hasNext());

        log.info("Completed Full Product Re-indexing job successfully.");
    }
}
