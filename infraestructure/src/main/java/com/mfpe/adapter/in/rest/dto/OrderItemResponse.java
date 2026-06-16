package com.mfpe.adapter.in.rest.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        String currency
) {
}
