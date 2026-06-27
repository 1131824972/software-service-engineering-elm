package com.ynu.elm.order.client;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.BusinessView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// OpenFeign 通过 Eureka 中的服务名调用 business-service，不在代码中写死主机和端口。
@FeignClient(name = "business-service", fallback = BusinessClientFallback.class)
public interface BusinessClient {
    // 创建订单时，订单服务通过该远程接口获取商家快照。
    @GetMapping("/api/business/{businessId}")
    ApiResponse<BusinessView> detail(@PathVariable("businessId") Long businessId);
}
