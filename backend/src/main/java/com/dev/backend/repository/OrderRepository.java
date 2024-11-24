package com.dev.backend.repository;

import com.dev.backend.entity.Order;
import com.dev.backend.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findById(String id);
    Page<Order> findAll(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE :userId IS NULL OR o.user.id = :userId")
    Page<Order> findByUserId(String userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) " +
            "AND (:userId IS NULL OR o.user.id = :userId)")
    Page<Order> searchOrders(@Param("status") OrderStatus status,
                             @Param("userId") String userId,
                             Pageable pageable);
}
