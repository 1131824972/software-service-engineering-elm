package com.ynu.elm.order.client;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.CartItemView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// OpenFeign 通过服务发现和负载均衡，把购物车相关操作委托给 cart-service。
@FeignClient(name = "cart-service", fallback = CartClientFallback.class)
public interface CartClient {
    // 读取购物车明细，作为生成订单明细的数据来源。
    @GetMapping("/api/cart")
    ApiResponse<List<CartItemView>> list(@RequestParam("userId") String userId, @RequestParam("businessId") Long businessId);

    // 订单创建成功后清空对应购物车。
    @DeleteMapping("/api/cart/clear")
    ApiResponse<Boolean> clear(@RequestParam("userId") String userId, @RequestParam("businessId") Long businessId);
}
