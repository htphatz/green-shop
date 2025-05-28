package com.dev.backend.repository;

import com.dev.backend.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    Optional<OrderItem> findById(String id);
    Page<OrderItem> findAll(Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    Page<OrderItem> findByOrderId(@Param("orderId") String orderId, Pageable pageable);

    @Query(value = "SELECT p.name, SUM(oi.total_money) AS revenue " +
            "FROM order_items oi " +
            "JOIN products p ON oi.product_id = p.id " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.status = 'PAID' " +
            "AND oi.created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY p.id, p.name", nativeQuery = true)
    List<Object[]> getRevenueByProduct(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT c.name, SUM(oi.total_money) AS revenue " +
            "FROM order_items oi " +
            "JOIN products p ON oi.product_id = p.id " +
            "JOIN categories c ON p.category_id = c.id " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.status = 'PAID' " +
            "AND oi.created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY c.id, c.name", nativeQuery = true)
    List<Object[]> getRevenueByCategory(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
}
