package com.dev.backend.service;

import com.dev.backend.dto.request.ChangeOrderStatusReq;
import com.dev.backend.dto.request.OrderItemReq;
import com.dev.backend.dto.request.OrderReq;
import com.dev.backend.dto.response.OrderRes;
import com.dev.backend.entity.*;
import com.dev.backend.enums.OrderStatus;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.OrderItemMapper;
import com.dev.backend.mapper.OrderMapper;
import com.dev.backend.repository.*;
import com.dev.backend.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Create Order: Success with sufficient stock and Kafka notification")
    void createOrder_Success() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");

        User user = User.builder().id("user-1").email("user@example.com").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Product product = Product.builder()
                .id("prod-1")
                .name("Monstera Deliciosa")
                .price(BigDecimal.valueOf(150000))
                .quantity(10)
                .soldQuantity(2)
                .build();
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));

        OrderItemReq itemReq = OrderItemReq.builder().productId("prod-1").quantity(3).build();
        OrderReq orderReq = OrderReq.builder()
                .fullName("John Doe")
                .phone("0987654321")
                .address("123 Green Street")
                .orderItems(List.of(itemReq))
                .build();

        Order order = Order.builder().id("order-100").build();
        when(orderMapper.toOrder(orderReq)).thenReturn(order);

        OrderItem orderItem = OrderItem.builder().id("item-1").quantity(3).build();
        when(orderItemMapper.toOrderItem(itemReq)).thenReturn(orderItem);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId("order-100");
            return o;
        });

        when(kafkaTemplate.send(eq("order-confirmed"), any(), any()))
                .thenReturn(new CompletableFuture<>());

        OrderRes expectedRes = OrderRes.builder()
                .id("order-100")
                .fullName("John Doe")
                .totalMoney(BigDecimal.valueOf(450000.0))
                .status(OrderStatus.PENDING)
                .build();
        when(orderMapper.toOrderRes(any(Order.class))).thenReturn(expectedRes);

        OrderRes res = orderService.createOrder(orderReq);

        assertThat(res).isNotNull();
        assertThat(res.getId()).isEqualTo("order-100");
        assertThat(product.getSoldQuantity()).isEqualTo(5);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(anyList());
        verify(productRepository, times(1)).saveAll(anyList());
        verify(kafkaTemplate, times(1)).send(eq("order-confirmed"), eq("order-100"), any());
    }

    @Test
    @DisplayName("Create Order: Throws OUT_OF_STOCK when requested quantity exceeds inventory")
    void createOrder_OutOfStock_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");

        User user = User.builder().id("user-1").email("user@example.com").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Product product = Product.builder()
                .id("prod-1")
                .quantity(10)
                .soldQuantity(9)
                .build();
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));

        OrderItemReq itemReq = OrderItemReq.builder().productId("prod-1").quantity(2).build();
        OrderReq orderReq = OrderReq.builder()
                .orderItems(List.of(itemReq))
                .build();

        Order order = Order.builder().build();
        when(orderMapper.toOrder(orderReq)).thenReturn(order);

        AppException ex = assertThrows(AppException.class, () -> orderService.createOrder(orderReq));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("Update Order Status: Throws CANNOT_CANCEL_SHIPPED_ORDER when trying to cancel shipped order")
    void updateOrderStatus_ShippedToCancelled_ThrowsException() {
        Order order = Order.builder()
                .id("order-1")
                .status(OrderStatus.SHIPPED)
                .build();
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        ChangeOrderStatusReq req = ChangeOrderStatusReq.builder()
                .status(OrderStatus.CANCELED)
                .build();

        AppException ex = assertThrows(AppException.class, () -> orderService.updateOrderStatus("order-1", req));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CANNOT_CANCEL_SHIPPED_ORDER);
    }

    @Test
    @DisplayName("Get Order By Id: Throws ORDER_NOT_FOUND when order does not exist")
    void getOrderById_NotFound_ThrowsException() {
        when(orderRepository.findById("nonexistent")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> orderService.getOrderById("nonexistent"));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
    }
}
