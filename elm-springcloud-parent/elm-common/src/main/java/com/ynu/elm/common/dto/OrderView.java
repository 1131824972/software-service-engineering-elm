package com.ynu.elm.common.dto;

import java.util.List;

public record OrderView(
        Long id,
        String userId,
        Long businessId,
        String businessName,
        Long addressId,
        String addressText,
        int totalPrice,
        String status,
        String payMethod,
        List<OrderItemView> items
) {
}
