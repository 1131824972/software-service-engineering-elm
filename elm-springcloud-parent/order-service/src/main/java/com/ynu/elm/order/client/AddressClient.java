package com.ynu.elm.order.client;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.AddressView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// OpenFeign 调用 address-service，获取用户选择的配送地址。
@FeignClient(name = "address-service", fallback = AddressClientFallback.class)
public interface AddressClient {
    // 订单保存地址快照，后续修改地址不会影响已生成订单。
    @GetMapping("/api/address/{addressId}")
    ApiResponse<AddressView> detail(@PathVariable("addressId") Long addressId);
}
