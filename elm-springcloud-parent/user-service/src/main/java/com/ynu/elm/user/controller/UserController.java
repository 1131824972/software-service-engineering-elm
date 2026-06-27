package com.ynu.elm.user.controller;

import com.ynu.elm.common.ApiResponse;
import com.ynu.elm.common.dto.LoginRequest;
import com.ynu.elm.common.dto.RegisterRequest;
import com.ynu.elm.common.dto.UserView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final Map<String, UserAccount> users = new ConcurrentHashMap<>();

    public UserController() {
        users.put("10001", new UserAccount("10001", "123456", "张三", "13800000001"));
    }

    @PostMapping("/register")
    public ApiResponse<UserView> register(@RequestBody RegisterRequest request) {
        if (request.userId() == null || request.userId().isBlank()) {
            return ApiResponse.fail(400, "userId is required");
        }
        if (users.containsKey(request.userId())) {
            return ApiResponse.fail(409, "user already exists");
        }
        UserAccount account = new UserAccount(request.userId(), request.password(), request.userName(), request.phone());
        users.put(account.userId(), account);
        return ApiResponse.ok(account.toView());
    }

    @PostMapping("/login")
    public ApiResponse<UserView> login(@RequestBody LoginRequest request) {
        UserAccount account = users.get(request.userId());
        if (account == null || !account.password().equals(request.password())) {
            return ApiResponse.fail(401, "invalid userId or password");
        }
        return ApiResponse.ok(account.toView());
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserView> detail(@PathVariable String userId) {
        UserAccount account = users.get(userId);
        if (account == null) {
            return ApiResponse.fail(404, "user not found");
        }
        return ApiResponse.ok(account.toView());
    }

    private record UserAccount(String userId, String password, String userName, String phone) {
        UserView toView() {
            return new UserView(userId, userName, phone);
        }
    }
}
