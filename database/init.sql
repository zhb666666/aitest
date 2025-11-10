-- 创建数据库
CREATE DATABASE IF NOT EXISTS trading_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE trading_platform;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    status INT DEFAULT 1 COMMENT '0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    status INT DEFAULT 1 COMMENT '0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 权限表
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(50) NOT NULL,
    permission_code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    resource_type VARCHAR(20) COMMENT 'menu, button',
    resource_url VARCHAR(255),
    status INT DEFAULT 1 COMMENT '0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 菜单表
CREATE TABLE IF NOT EXISTS menus (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_name VARCHAR(50) NOT NULL,
    menu_code VARCHAR(50),
    path VARCHAR(255),
    component VARCHAR(255),
    icon VARCHAR(50),
    sort INT DEFAULT 0,
    parent_id INT DEFAULT 0,
    status INT DEFAULT 1 COMMENT '0-禁用 1-启用',
    menu_type VARCHAR(20) COMMENT 'menu, button',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    image VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    stock INT DEFAULT 0,
    seller_id BIGINT NOT NULL,
    category VARCHAR(50),
    status INT DEFAULT 1 COMMENT '0-下架 1-上架',
    sales INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_seller_id (seller_id),
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 购物车表
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status INT DEFAULT 0 COMMENT '0-待支付 1-已支付 2-已发货 3-已完成 4-已取消',
    address VARCHAR(255),
    receiver VARCHAR(50),
    phone VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单项表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(100),
    price DECIMAL(10, 2),
    quantity INT,
    subtotal DECIMAL(10, 2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 支付表
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_no VARCHAR(50) UNIQUE NOT NULL,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(20) COMMENT 'alipay, wechat, bank',
    status INT DEFAULT 0 COMMENT '0-待支付 1-已支付 2-已退款',
    transaction_id VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_payment_no (payment_no),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入初始数据
-- 插入默认角色
INSERT INTO roles (role_name, role_code, description, status) VALUES
('管理员', 'ADMIN', '系统管理员', 1),
('普通用户', 'USER', '普通用户', 1);

-- 插入默认权限
INSERT INTO permissions (permission_name, permission_code, description, resource_type, status) VALUES
('用户列表', 'user:list', '查看用户列表', 'button', 1),
('用户查看', 'user:view', '查看用户详情', 'button', 1),
('用户添加', 'user:add', '添加用户', 'button', 1),
('用户编辑', 'user:edit', '编辑用户', 'button', 1),
('用户删除', 'user:delete', '删除用户', 'button', 1),
('角色列表', 'role:list', '查看角色列表', 'button', 1),
('角色查看', 'role:view', '查看角色详情', 'button', 1),
('角色添加', 'role:add', '添加角色', 'button', 1),
('角色编辑', 'role:edit', '编辑角色', 'button', 1),
('角色删除', 'role:delete', '删除角色', 'button', 1),
('权限列表', 'permission:list', '查看权限列表', 'button', 1),
('权限查看', 'permission:view', '查看权限详情', 'button', 1),
('权限添加', 'permission:add', '添加权限', 'button', 1),
('权限编辑', 'permission:edit', '编辑权限', 'button', 1),
('权限删除', 'permission:delete', '删除权限', 'button', 1),
('菜单列表', 'menu:list', '查看菜单列表', 'button', 1),
('菜单查看', 'menu:view', '查看菜单详情', 'button', 1),
('菜单添加', 'menu:add', '添加菜单', 'button', 1),
('菜单编辑', 'menu:edit', '编辑菜单', 'button', 1),
('菜单删除', 'menu:delete', '删除菜单', 'button', 1),
('商品列表', 'product:list', '查看商品列表', 'button', 1),
('商品添加', 'product:add', '添加商品', 'button', 1),
('商品编辑', 'product:edit', '编辑商品', 'button', 1),
('商品删除', 'product:delete', '删除商品', 'button', 1);

-- 将权限分配给管理员角色
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO users (username, password, email, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pQ5O', 'admin@example.com', 1);

-- 将管理员角色分配给admin用户
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

