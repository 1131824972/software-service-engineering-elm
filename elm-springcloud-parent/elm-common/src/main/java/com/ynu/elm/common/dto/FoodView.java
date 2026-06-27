package com.ynu.elm.common.dto;

public record FoodView(Long id, Long businessId, String name, String description, int price, int quantity) {
}
