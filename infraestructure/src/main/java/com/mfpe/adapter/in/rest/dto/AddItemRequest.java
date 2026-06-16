package com.mfpe.adapter.in.rest.dto;

import java.math.BigDecimal;

/** DTO de entrada para agregar un item al pedido. */
public record AddItemRequest(
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        String currency
) { }
