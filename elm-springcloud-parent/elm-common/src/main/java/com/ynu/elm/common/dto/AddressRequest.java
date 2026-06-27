package com.ynu.elm.common.dto;

public record AddressRequest(
        String userId,
        String contactName,
        String contactSex,
        String contactPhone,
        String address
) {
}
