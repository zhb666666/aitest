package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.User;
import com.trading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:list')")
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:view')")
    public Result<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(Result::success)
                .orElse(Result.error("用户不存在"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:add')")
    public Result<User> createUser(@RequestBody User user) {
        try {
            return Result.success(userService.createUser(user));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return Result.success(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success(null);
    }
}

