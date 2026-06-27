package com.ynu.elm.common.dto;

public record CartAddRequest(
        String userId,
        Long businessId,
        Long foodId,
        String foodName,
        int price,
        int quantity
) {
}
