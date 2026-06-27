package com.ynu.elm.common.dto;

public record CartItemView(
        Long id,
        String userId,
        Long businessId,
        Long foodId,
        String foodName,
        int price,
        int quantity
) {
}
