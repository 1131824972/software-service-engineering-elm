package com.ynu.elm.cart.controller;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.CartAddRequest;
import com.ynu.elm.common.dto.CartItemView;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final List<CartItemView> items = new ArrayList<>();

    public CartController() {
        items.add(new CartItemView(idGenerator.getAndIncrement(), "10001", 1L, 1L, "纯肉鲜肉（水饺）", 15, 2));
        items.add(new CartItemView(idGenerator.getAndIncrement(), "10001", 1L, 2L, "玉米鲜肉（水饺）", 16, 1));
    }

    @GetMapping
    public ApiResponse<List<CartItemView>> list(@RequestParam String userId, @RequestParam(required = false) Long businessId) {
        return ApiResponse.ok(items.stream()
                .filter(item -> item.userId().equals(userId))
                .filter(item -> businessId == null || item.businessId().equals(businessId))
                .toList());
    }

    @PostMapping
    public ApiResponse<List<CartItemView>> add(@RequestBody CartAddRequest request) {
        CartItemView existed = items.stream()
                .filter(item -> item.userId().equals(request.userId()))
                .filter(item -> item.businessId().equals(request.businessId()))
                .filter(item -> item.foodId().equals(request.foodId()))
                .findFirst()
                .orElse(null);
        if (existed != null) {
            items.remove(existed);
            items.add(new CartItemView(existed.id(), existed.userId(), existed.businessId(), existed.foodId(), existed.foodName(), existed.price(), existed.quantity() + request.quantity()));
        } else {
            items.add(new CartItemView(idGenerator.getAndIncrement(), request.userId(), request.businessId(), request.foodId(), request.foodName(), request.price(), request.quantity()));
        }
        return list(request.userId(), request.businessId());
    }

    @PostMapping("/{itemId}/decrease")
    public ApiResponse<List<CartItemView>> decrease(@PathVariable Long itemId) {
        CartItemView existed = items.stream().filter(item -> item.id().equals(itemId)).findFirst().orElse(null);
        if (existed == null) {
            return ApiResponse.fail(404, "cart item not found");
        }
        items.remove(existed);
        if (existed.quantity() > 1) {
            items.add(new CartItemView(existed.id(), existed.userId(), existed.businessId(), existed.foodId(), existed.foodName(), existed.price(), existed.quantity() - 1));
        }
        return list(existed.userId(), existed.businessId());
    }

    @DeleteMapping("/clear")
    public ApiResponse<Boolean> clear(@RequestParam String userId, @RequestParam Long businessId) {
        items.removeIf(item -> item.userId().equals(userId) && item.businessId().equals(businessId));
        return ApiResponse.ok(true);
    }
}
