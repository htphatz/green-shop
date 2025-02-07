package com.dev.backend.repository;

import com.dev.backend.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private static final String LIKE_FORMAT = "%%%s%%";
    private static final String SEARCH_OPERATOR = "(\\w+?)(:|<|>)(.*)";
    private static final String SORT_BY = "(\\w+?)(:)(.*)";

    public Page<Product> searchByCustomQuery(Integer pageNumber, Integer pageSize, String sortBy, String keyword) {
        log.info("Execute search Product with keyword={}", keyword);

        StringBuilder sqlQuery = new StringBuilder("SELECT p FROM Product p WHERE 1=1");
        if (StringUtils.hasLength(keyword)) {
            sqlQuery.append(" AND LOWER(p.name) LIKE LOWER(:name)");
            sqlQuery.append(" OR LOWER(p.description) LIKE LOWER(:description)");
        }

        if (StringUtils.hasLength(sortBy)) {
            // price:asc|desc
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sqlQuery.append(String.format(" ORDER BY p.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        // Get list of Product
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        if (StringUtils.hasLength(keyword)) {
            selectQuery.setParameter("name", String.format(LIKE_FORMAT, keyword));
            selectQuery.setParameter("description", String.format(LIKE_FORMAT, keyword));
        }
        selectQuery.setFirstResult(pageNumber);
        selectQuery.setMaxResults(pageSize);
        List<?> products = selectQuery.getResultList();

        // Count Product
        StringBuilder sqlCountQuery = new StringBuilder("SELECT COUNT(*) FROM Product p");
        if (StringUtils.hasLength(keyword)) {
            sqlCountQuery.append(" WHERE LOWER(p.name) LIKE LOWER(?1)");
            sqlCountQuery.append(" OR LOWER(p.description) LIKE LOWER(?2)");
        }

        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());
        if (StringUtils.hasLength(keyword)) {
            countQuery.setParameter(1, String.format(LIKE_FORMAT, keyword));
            countQuery.setParameter(2, String.format(LIKE_FORMAT, keyword));
            countQuery.getSingleResult();
        }

        Long totalElements = (Long) countQuery.getSingleResult();
        log.info("totalElements={}", totalElements);

        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return new PageImpl<>((List<Product>) products, pageable, totalElements);
    }

    public void searchByCriteria(Integer pageNumber, Integer pageSize, String sortBy, String... search) {

    }
}
