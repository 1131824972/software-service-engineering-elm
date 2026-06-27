package com.ynu.elm.order.client;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.CartItemView;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartClientFallback implements CartClient {
    @Override
    public ApiResponse<List<CartItemView>> list(String userId, Long businessId) {
        // 返回受控的 503 响应，避免把远程调用异常继续向外抛出。
        return ApiResponse.fail(503, "cart-service fallback");
    }

    @Override
    public ApiResponse<Boolean> clear(String userId, Long businessId) {
        // 清空购物车操作同样进行降级处理，方便调用方感知 cart-service 不可用。
        return ApiResponse.fail(503, "cart-service fallback");
    }
}
