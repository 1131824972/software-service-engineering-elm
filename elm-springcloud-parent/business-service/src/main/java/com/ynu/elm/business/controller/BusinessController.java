package com.ynu.elm.business.controller;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.BusinessView;
import com.ynu.elm.common.dto.CategoryView;
import com.ynu.elm.common.dto.FoodView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RefreshScope
@RestController
@RequestMapping("/api/business")
public class BusinessController {
    @Value("${server.port}")
    private String port;

    @Value("${business.notice:本地默认配置}")
    private String notice;

    private final List<CategoryView> categories = List.of(
            new CategoryView(1L, "美食", "food"),
            new CategoryView(2L, "早餐", "breakfast"),
            new CategoryView(3L, "跑腿代购", "delivery"),
            new CategoryView(4L, "汉堡披萨", "burger"),
            new CategoryView(5L, "甜品饮品", "drink")
    );

    private final List<BusinessView> businesses = List.of(
            new BusinessView(1L, 1L, "万家饺子（软件园E18店）", "各种饺子、炒菜、家常菜", 4.9, 345, 15, 3, "30分钟", 2),
            new BusinessView(2L, 1L, "小锅饭豆腐馆（全运店）", "米饭套餐、豆腐锅、东北菜", 4.7, 108, 15, 3, "35分钟", 0),
            new BusinessView(3L, 4L, "麦当劳麦乐送（全运路店）", "汉堡、小食、饮品、套餐", 4.8, 260, 15, 3, "28分钟", 1),
            new BusinessView(4L, 2L, "米村拌饭（浑南店）", "拌饭、汤饭、韩式小食", 4.6, 189, 15, 3, "32分钟", 0)
    );

    private final List<FoodView> foods = List.of(
            new FoodView(1L, 1L, "纯肉鲜肉（水饺）", "新鲜猪肉手工水饺", 15, 0),
            new FoodView(2L, 1L, "玉米鲜肉（水饺）", "玉米鲜肉水饺", 16, 0),
            new FoodView(3L, 1L, "虾仁三鲜（蒸饺）", "虾仁三鲜蒸饺", 22, 0),
            new FoodView(4L, 1L, "素三鲜（蒸饺）", "韭菜鸡蛋素三鲜", 15, 0),
            new FoodView(5L, 2L, "招牌小锅饭", "热气小锅饭套餐", 18, 0),
            new FoodView(6L, 3L, "巨无霸套餐", "汉堡薯条饮品组合", 39, 0),
            new FoodView(7L, 4L, "石锅拌饭", "经典韩式拌饭", 24, 0)
    );

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.ok("business-service running on port " + port);
    }

    @GetMapping("/config")
    public ApiResponse<String> config() {
        return ApiResponse.ok("port=" + port + ", notice=" + notice);
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryView>> categories() {
        return ApiResponse.ok(categories);
    }

    @GetMapping("/list")
    public ApiResponse<List<BusinessView>> list(@RequestParam(required = false) Long categoryId) {
        if (categoryId == null) {
            return ApiResponse.ok(businesses);
        }
        return ApiResponse.ok(businesses.stream()
                .filter(item -> item.categoryId().equals(categoryId))
                .toList());
    }

    @GetMapping("/{businessId}")
    public ApiResponse<BusinessView> detail(@PathVariable Long businessId) {
        return businesses.stream()
                .filter(item -> item.id().equals(businessId))
                .findFirst()
                .map(ApiResponse::ok)
                .orElseGet(() -> ApiResponse.fail(404, "business not found"));
    }

    @GetMapping("/{businessId}/foods")
    public ApiResponse<List<FoodView>> foods(@PathVariable Long businessId) {
        return ApiResponse.ok(foods.stream()
                .filter(item -> item.businessId().equals(businessId))
                .toList());
    }
}