package com.mfpe.service;

import com.mfpe.exception.OrderNotFoundException;
import com.mfpe.model.entity.Order;
import com.mfpe.model.vo.OrderId;
import com.mfpe.port.out.FindOrderByIdPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetOrderByIdServiceTest {

    @Mock
    private FindOrderByIdPort findOrderByIdPort;

    @Mock
    private Order order;

    private GetOrderByIdService service;

    @BeforeEach
    void  setUp() {
        service = new GetOrderByIdService(findOrderByIdPort);
    }

    @Test
    void getOrderById_shoulThrowExceptionWhenOrderDoesNotExist() {
        String orderId = "16dfc7aa-71ba-4017-ad95-b263503d20f8";

        when(findOrderByIdPort.findById(any(OrderId.class)))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> service.getOrderById(orderId)
        );

        verify(findOrderByIdPort).findById(any(OrderId.class));
    }
}
