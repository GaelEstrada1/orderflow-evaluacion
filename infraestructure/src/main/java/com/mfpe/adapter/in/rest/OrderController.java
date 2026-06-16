package com.mfpe.adapter.in.rest;

import com.mfpe.adapter.in.rest.dto.AddItemRequest;
import com.mfpe.adapter.in.rest.dto.CreateOrderRequest;
import com.mfpe.adapter.in.rest.dto.OrderResponse;
import com.mfpe.adapter.in.rest.dto.OrderResponseMapper;
import com.mfpe.command.AddItemToOrderCommand;
import com.mfpe.command.CreateOrderCommand;
import com.mfpe.model.entity.Order;
import com.mfpe.port.in.AddItemToOrderUseCase;
import com.mfpe.port.in.CancelOrderUseCase;
import com.mfpe.port.in.CreateOrderUseCase;
import com.mfpe.port.in.PayOrderUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final AddItemToOrderUseCase addItemToOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final OrderResponseMapper responseMapper;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           AddItemToOrderUseCase addItemToOrderUseCase,
                           PayOrderUseCase payOrderUseCase,
                           CancelOrderUseCase cancelOrderUseCase,
                           OrderResponseMapper responseMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.addItemToOrderUseCase = addItemToOrderUseCase;
        this.payOrderUseCase = payOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest req){

        Order order = createOrderUseCase.createOrder(
                new CreateOrderCommand(req.customerId()));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId().toString())
                .toUri();

        return ResponseEntity.created(location).body(responseMapper.toResponse(order));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponse> addItem(
            @PathVariable String id,
            @RequestBody AddItemRequest req){

        Order order = addItemToOrderUseCase.addItem(new AddItemToOrderCommand(
                id,
                req.productId(),
                req.productName(),
                req.quantity(),
                req.unitPrice(),
                req.currency()
        ));

        return ResponseEntity.ok(responseMapper.toResponse(order));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> payOrder(@PathVariable String id){
        payOrderUseCase.payOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id){
        cancelOrderUseCase.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

}