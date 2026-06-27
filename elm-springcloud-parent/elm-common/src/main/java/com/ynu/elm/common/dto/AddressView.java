package com.ynu.elm.common.dto;

public record AddressView(
        Long id,
        String userId,
        String contactName,
        String contactSex,
        String contactPhone,
        String address
) {
}
