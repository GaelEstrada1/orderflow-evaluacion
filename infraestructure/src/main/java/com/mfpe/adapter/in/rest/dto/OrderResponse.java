package com.mfpe.adapter.in.rest.dto;

import com.mfpe.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** DTO de salida. Nunca se expone la entidad de dominio directamente. */
public record OrderResponse(
    String id,
    String customerId,
    OrderStatus status,
    BigDecimal totalAmount,
    String totalCurrency,
    LocalDateTime createdAt,
    List<OrderItemResponse> items
) { }
