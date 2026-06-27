package com.ynu.elm.address.controller;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.AddressRequest;
import com.ynu.elm.common.dto.AddressView;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/address")
public class AddressController {
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final List<AddressView> addresses = new ArrayList<>();

    public AddressController() {
        addresses.add(new AddressView(idGenerator.getAndIncrement(), "10001", "张三", "男", "13800000001", "沈阳市浑南区软件园E18"));
        addresses.add(new AddressView(idGenerator.getAndIncrement(), "10001", "李四", "女", "13800000002", "沈阳市浑南区创新路1号"));
    }

    @GetMapping
    public ApiResponse<List<AddressView>> list(@RequestParam String userId) {
        return ApiResponse.ok(addresses.stream()
                .filter(item -> item.userId().equals(userId))
                .toList());
    }

    @GetMapping("/{addressId}")
    public ApiResponse<AddressView> detail(@PathVariable Long addressId) {
        return addresses.stream()
                .filter(item -> item.id().equals(addressId))
                .findFirst()
                .map(ApiResponse::ok)
                .orElseGet(() -> ApiResponse.fail(404, "address not found"));
    }

    @PostMapping
    public ApiResponse<AddressView> add(@RequestBody AddressRequest request) {
        AddressView address = new AddressView(idGenerator.getAndIncrement(), request.userId(), request.contactName(), request.contactSex(), request.contactPhone(), request.address());
        addresses.add(address);
        return ApiResponse.ok(address);
    }

    @PutMapping("/{addressId}")
    public ApiResponse<AddressView> update(@PathVariable Long addressId, @RequestBody AddressRequest request) {
        AddressView existed = addresses.stream().filter(item -> item.id().equals(addressId)).findFirst().orElse(null);
        if (existed == null) {
            return ApiResponse.fail(404, "address not found");
        }
        addresses.remove(existed);
        AddressView address = new AddressView(addressId, request.userId(), request.contactName(), request.contactSex(), request.contactPhone(), request.address());
        addresses.add(address);
        return ApiResponse.ok(address);
    }

    @DeleteMapping("/{addressId}")
    public ApiResponse<Boolean> delete(@PathVariable Long addressId) {
        return ApiResponse.ok(addresses.removeIf(item -> item.id().equals(addressId)));
    }
}
