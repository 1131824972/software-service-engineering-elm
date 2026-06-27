package com.ynu.elm.common.dto;

public record OrderCreateRequest(String userId, Long businessId, Long addressId) {
}
