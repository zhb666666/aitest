package com.trading.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类 - 用于生成BCrypt密码哈希
 * 运行此类的main方法可以生成指定密码的BCrypt哈希值
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String encodedPassword = encoder.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + encodedPassword);
        
        // 验证
        boolean matches = encoder.matches(password, encodedPassword);
        System.out.println("验证结果: " + matches);
    }
}

