package com.dev.backend.repository;

import com.dev.backend.entity.Category;
import com.dev.backend.entity.Product;
import com.dev.backend.repository.criteria.ProductSearchCriteriaConsumer;
import com.dev.backend.repository.criteria.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
    private static final String SORT_BY = "(\\w+?)(:)(asc|desc)";

    public Page<Product> searchByCustomQuery(Integer pageNumber, Integer pageSize, String sortBy, String keyword) {
        log.info("Execute search Product with keyword={}", keyword);

        StringBuilder sqlQuery = new StringBuilder("SELECT p FROM Product p WHERE 1=1");
        if (StringUtils.hasLength(keyword)) {
            sqlQuery.append(" AND (LOWER(p.name) LIKE LOWER(:name)");
            sqlQuery.append(" OR LOWER(p.description) LIKE LOWER(:description))");
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
        Query selectQuery = entityManager.createQuery(sqlQuery.toString(), Product.class);
        if (StringUtils.hasLength(keyword)) {
            selectQuery.setParameter("name", String.format(LIKE_FORMAT, keyword));
            selectQuery.setParameter("description", String.format(LIKE_FORMAT, keyword));
        }
        int offset = Math.max(0, (pageNumber - 1) * pageSize);
        selectQuery.setFirstResult(offset);
        selectQuery.setMaxResults(pageSize);
        List<Product> products = (List<Product>) selectQuery.getResultList();

        // Count Product
        StringBuilder sqlCountQuery = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE 1=1");
        if (StringUtils.hasLength(keyword)) {
            sqlCountQuery.append(" AND (LOWER(p.name) LIKE LOWER(:name)");
            sqlCountQuery.append(" OR LOWER(p.description) LIKE LOWER(:description))");
        }

        Query countQuery = entityManager.createQuery(sqlCountQuery.toString(), Long.class);
        if (StringUtils.hasLength(keyword)) {
            countQuery.setParameter("name", String.format(LIKE_FORMAT, keyword));
            countQuery.setParameter("description", String.format(LIKE_FORMAT, keyword));
        }

        Long totalElements = (Long) countQuery.getSingleResult();
        log.info("totalElements={}", totalElements);

        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        return new PageImpl<>(products, pageable, totalElements);
    }

    public Page<Product> searchByCriteria(Integer pageNumber, Integer pageSize, String sortBy, String categoryId, String... search) {
        List<SearchCriteria> criteriaList = new ArrayList<>();

        if (search != null) {
            Pattern pattern = Pattern.compile(SEARCH_OPERATOR);
            for (String param : search) {
                // price>2000000
                Matcher matcher = pattern.matcher(param);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        List<Product> products = getProducts(pageNumber, pageSize, sortBy, categoryId, criteriaList);
        Long totalElements = getTotalElements(categoryId, criteriaList);

        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        return new PageImpl<>(products, pageable, totalElements);
    }

    private List<Product> getProducts(Integer pageNumber, Integer pageSize, String sortBy, String categoryId, List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = builder.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        // Xử lý điều kiện tìm kiếm
        Predicate predicate = builder.conjunction();
        ProductSearchCriteriaConsumer consumer = new ProductSearchCriteriaConsumer(builder, predicate, root);
        criteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        // Xử lý Product join Category
        if (StringUtils.hasLength(categoryId)) {
            Join<Category, Product> categoryProductJoin = root.join("category");
            Predicate categoryPredicate = builder.equal(categoryProductJoin.get("id"), categoryId);
            predicate = builder.and(predicate, categoryPredicate);
        }
        query.where(predicate);

        // Xử lý sắp xếp
        if (StringUtils.hasLength(sortBy)) {
            // price:asc|desc
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("desc")) {
                    query.orderBy(builder.desc(root.get(columnName)));
                } else {
                    query.orderBy(builder.asc(root.get(columnName)));
                }
            }
        }

        int offset = Math.max(0, (pageNumber - 1) * pageSize);
        return entityManager.createQuery(query)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    private Long getTotalElements(String categoryId, List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = builder.conjunction();
        ProductSearchCriteriaConsumer consumer = new ProductSearchCriteriaConsumer(builder, predicate, root);
        criteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        if (StringUtils.hasLength(categoryId)) {
            Join<Category, Product> categoryProductJoin = root.join("category");
            Predicate categoryPredicate = builder.equal(categoryProductJoin.get("id"), categoryId);
            predicate = builder.and(predicate, categoryPredicate);
        }

        query.select(builder.count(root));
        query.where(predicate);

        return entityManager.createQuery(query).getSingleResult();
    }
}
