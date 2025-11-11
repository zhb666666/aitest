package com.trading.config;

import com.trading.entity.Role;
import com.trading.entity.User;
import com.trading.repository.RoleRepository;
import com.trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 数据初始化类 - 如果数据库中没有admin用户，会自动创建
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否存在admin用户
        if (!userRepository.findByUsername("admin").isPresent()) {
            // 确保存在管理员角色
            Role adminRole = roleRepository.findByRoleCode("ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName("管理员");
                        role.setRoleCode("ADMIN");
                        role.setDescription("系统管理员");
                        role.setStatus(1);
                        return roleRepository.save(role);
                    });

            // 创建admin用户
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setStatus(1);

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            System.out.println("已创建默认管理员用户: admin/admin123");
        }
    }
}

