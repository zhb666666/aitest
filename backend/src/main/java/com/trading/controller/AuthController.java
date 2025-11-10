package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.User;
import com.trading.service.UserService;
import com.trading.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public Result<Map<String, String>> register(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            String token = jwtUtil.generateToken(savedUser.getUsername());
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            data.put("username", savedUser.getUsername());
            return Result.success("注册成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            String token = jwtUtil.generateToken(username);
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            data.put("username", username);
            return Result.success("登录成功", data);
        } catch (Exception e) {
            return Result.error("用户名或密码错误");
        }
    }
}

