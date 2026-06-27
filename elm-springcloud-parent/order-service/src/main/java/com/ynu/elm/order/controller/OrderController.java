package com.ynu.elm.order.controller;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.AddressView;
import com.ynu.elm.common.dto.BusinessView;
import com.ynu.elm.common.dto.CartItemView;
import com.ynu.elm.common.dto.OrderCreateRequest;
import com.ynu.elm.common.dto.OrderItemView;
import com.ynu.elm.common.dto.OrderPayRequest;
import com.ynu.elm.common.dto.OrderView;
import com.ynu.elm.order.client.AddressClient;
import com.ynu.elm.order.client.BusinessClient;
import com.ynu.elm.order.client.CartClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final List<OrderView> orders = new ArrayList<>();
    private final BusinessClient businessClient;
    private final CartClient cartClient;
    private final AddressClient addressClient;

    public OrderController(BusinessClient businessClient, CartClient cartClient, AddressClient addressClient) {
        this.businessClient = businessClient;
        this.cartClient = cartClient;
        this.addressClient = addressClient;
    }

    @GetMapping
    public ApiResponse<List<OrderView>> list(@RequestParam String userId) {
        return ApiResponse.ok(orders.stream()
                .filter(order -> order.userId().equals(userId))
                .sorted(Comparator.comparing(OrderView::id).reversed())
                .toList());
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderView> detail(@PathVariable Long orderId) {
        return orders.stream()
                .filter(order -> order.id().equals(orderId))
                .findFirst()
                .map(ApiResponse::ok)
                .orElseGet(() -> ApiResponse.fail(404, "order not found"));
    }

    @PostMapping
    @CircuitBreaker(name = "orderCreateBreaker", fallbackMethod = "createDown")
    @Bulkhead(name = "orderCreateBulkhead", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "createDown")
    public ApiResponse<OrderView> create(@RequestBody OrderCreateRequest request) {
        // 跨服务编排：订单服务通过 OpenFeign 调用其他业务边界的服务。
        ApiResponse<BusinessView> businessResponse = businessClient.detail(request.businessId());
        BusinessView business = requireRemoteData(businessResponse, "business-service is unavailable");

        ApiResponse<AddressView> addressResponse = addressClient.detail(request.addressId());
        AddressView address = requireRemoteData(addressResponse, "address-service is unavailable");

        ApiResponse<List<CartItemView>> cartResponse = cartClient.list(request.userId(), request.businessId());
        List<CartItemView> cartItems = requireRemoteData(cartResponse, "cart-service is unavailable");
        if (cartItems.isEmpty()) {
            return ApiResponse.fail(400, "cart is empty");
        }

        // 根据商家、地址和购物车数据生成订单快照，避免后续数据变化影响历史订单。
        List<OrderItemView> items = cartItems.stream()
                .map(item -> new OrderItemView(item.foodId(), item.foodName(), item.price(), item.quantity()))
                .toList();
        int totalPrice = items.stream().mapToInt(item -> item.price() * item.quantity()).sum() + business.deliveryFee();
        String addressText = address.contactName() + " " + address.contactPhone() + " " + address.address();

        OrderView order = new OrderView(
                idGenerator.getAndIncrement(),
                request.userId(),
                request.businessId(),
                business.name(),
                request.addressId(),
                addressText,
                totalPrice,
                "UNPAID",
                null,
                items
        );
        orders.add(order);
        // 订单已保存到内存集合中，因此可以清空对应购物车。
        cartClient.clear(request.userId(), request.businessId());
        return ApiResponse.ok(order);
    }

    public ApiResponse<OrderView> createDown(OrderCreateRequest request, Throwable e) {
        // 与 Demo12 一致：被保护方法异常或被限流时，进入 fallbackMethod 返回降级响应。
        return ApiResponse.fail(503, "订单服务降级：远程服务当前异常，请稍后再试");
    }

    private static <T> T requireRemoteData(ApiResponse<T> response, String message) {
        if (response == null || response.code() != 200 || response.data() == null) {
            throw new RemoteServiceException(message);
        }
        return response.data();
    }

    private static class RemoteServiceException extends RuntimeException {
        private RemoteServiceException(String message) {
            super(message);
        }
    }

    @PostMapping("/{orderId}/pay")
    public ApiResponse<OrderView> pay(@PathVariable Long orderId, @RequestBody OrderPayRequest request) {
        OrderView existed = orders.stream().filter(order -> order.id().equals(orderId)).findFirst().orElse(null);
        if (existed == null) {
            return ApiResponse.fail(404, "order not found");
        }
        orders.remove(existed);
        OrderView paid = new OrderView(
                existed.id(),
                existed.userId(),
                existed.businessId(),
                existed.businessName(),
                existed.addressId(),
                existed.addressText(),
                existed.totalPrice(),
                "PAID",
                request.payMethod(),
                existed.items()
        );
        orders.add(paid);
        return ApiResponse.ok(paid);
    }

    @GetMapping("/fallback-demo")
    public ApiResponse<String> fallbackDemo() {
        return ApiResponse.ok("Stop business/cart/address service, then create order to trigger Feign fallback.");
    }
}
