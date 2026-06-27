package com.ynu.elm.order.client;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.AddressView;
import org.springframework.stereotype.Component;

@Component
public class AddressClientFallback implements AddressClient {
    @Override
    public ApiResponse<AddressView> detail(Long addressId) {
        // 地址服务查询失败时，转换为业务层可识别的降级响应。
        return ApiResponse.fail(503, "address-service fallback");
    }
}
