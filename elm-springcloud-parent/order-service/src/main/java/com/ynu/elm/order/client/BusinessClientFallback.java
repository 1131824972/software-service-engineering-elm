package com.ynu.elm.order.client;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.BusinessView;
import org.springframework.stereotype.Component;

@Component
public class BusinessClientFallback implements BusinessClient {
    @Override
    public ApiResponse<BusinessView> detail(Long businessId) {
        // business-service 不可用时返回受控失败结果，避免订单创建流程长时间阻塞。
        return ApiResponse.fail(503, "business-service fallback");
    }
}
