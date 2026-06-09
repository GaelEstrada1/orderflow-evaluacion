package com.mfpe.service;

import com.mfpe.exception.OrderNotFoundException;
import com.mfpe.model.entity.Order;
import com.mfpe.model.vo.OrderId;
import com.mfpe.port.in.CancelOrderUseCase;
import com.mfpe.port.out.FindOrderByIdPort;
import com.mfpe.port.out.SaveOrderPort;

/*
 * Servicio que implementa la lógica para cancelar una orden.
 * Este servicio se encarga de actualizarlo a "cancelada".
 */
public class CancelOrderService implements CancelOrderUseCase {

    private final FindOrderByIdPort findOrderByIdPort;
    private final SaveOrderPort saveOrderPort;

    public CancelOrderService(FindOrderByIdPort findOrderByIdPort,
                              SaveOrderPort saveOrderPort) {
        this.findOrderByIdPort = findOrderByIdPort;
        this.saveOrderPort = saveOrderPort;
    }

    @Override
    public void cancelOrder(String orderId) {
        Order order = findOrderByIdPort.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.cancel();
        saveOrderPort.save(order);
    }

}