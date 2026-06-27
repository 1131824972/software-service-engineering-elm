package com.ynu.elm.common.dto;

public record BusinessView(
        Long id,
        Long categoryId,
        String name,
        String description,
        double rating,
        int monthlySales,
        int startPrice,
        int deliveryFee,
        String deliveryTime,
        int cartQuantity
) {
}
