package com.mfpe.adapter.out.notification;

import com.mfpe.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SimulatedNotificationServiceTest {

    private final SimulatedNotificationService service =
            new SimulatedNotificationService();

    @Test
    void notifyOrderStatusChange_shouldNotThrowException() {
        assertDoesNotThrow(() ->
                service.notifyOrderStatusChange(
                        "order-123",
                        OrderStatus.PAID
                )
        );
    }
}
