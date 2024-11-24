package com.dev.backend.repository;

import com.dev.backend.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    Optional<OrderItem> findById(String id);
    Page<OrderItem> findAll(Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    Page<OrderItem> findByOrderId(@Param("orderId") String orderId, Pageable pageable);
}
