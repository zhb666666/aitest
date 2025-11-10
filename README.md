# 交易平台系统

一个基于 Vue 3 + Spring Boot 的完整交易平台系统，包含用户管理、角色权限、商品交易、购物车、订单支付等功能。

## 技术栈

### 后端
- Spring Boot 2.7.14
- Spring Security (JWT认证)
- Spring Data JPA
- MySQL 8.0
- Redis (缓存)
- RabbitMQ (消息队列)

### 前端
- Vue 3
- Vue Router 4
- Vuex 4
- Element Plus
- Axios

## 功能模块

### 1. 用户管理
- 用户注册/登录
- 用户信息管理
- 用户状态管理

### 2. 角色权限管理
- 角色管理（增删改查）
- 权限管理（增删改查）
- 角色权限分配
- 用户角色分配

### 3. 菜单管理
- 菜单树形结构管理
- 菜单权限控制

### 4. 商品管理
- 商品发布
- 商品浏览
- 商品搜索
- 商品分类

### 5. 购物车
- 添加商品到购物车
- 修改商品数量
- 删除购物车商品

### 6. 订单管理
- 创建订单
- 订单列表
- 订单状态管理
- 订单取消

### 7. 支付管理
- 创建支付记录
- 支付处理
- 支付状态查询

## 项目结构

```
trading-platform/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/trading/
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── repository/  # 数据访问层
│   │   │   │   ├── service/     # 业务逻辑层
│   │   │   │   ├── security/    # 安全配置
│   │   │   │   ├── util/        # 工具类
│   │   │   │   └── common/      # 通用类
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── pom.xml
├── frontend/               # 前端项目
│   ├── src/
│   │   ├── api/           # API接口
│   │   ├── assets/        # 静态资源
│   │   ├── components/    # 组件
│   │   ├── layouts/       # 布局
│   │   ├── router/        # 路由
│   │   ├── store/         # 状态管理
│   │   ├── views/         # 视图
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vue.config.js
├── database/              # 数据库脚本
│   └── init.sql
└── README.md
```

## 环境要求

- JDK 1.8+
- Maven 3.6+
- Node.js 14+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+

## 快速开始

### 1. 数据库初始化

```bash
# 创建数据库并导入初始化脚本
mysql -u root -p < database/init.sql
```

### 2. 配置后端

修改 `backend/src/main/resources/application.yml` 中的数据库、Redis、RabbitMQ配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/trading_platform
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 3. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 4. 启动前端

```bash
cd frontend
npm install
npm run serve
```

前端服务将在 `http://localhost:8081` 启动

## 默认账号

- 用户名: `admin`
- 密码: `admin123`

## API 接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

### 用户管理
- `GET /api/users` - 获取用户列表
- `GET /api/users/{id}` - 获取用户详情
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

### 角色管理
- `GET /api/roles` - 获取角色列表
- `POST /api/roles` - 创建角色
- `PUT /api/roles/{id}` - 更新角色
- `DELETE /api/roles/{id}` - 删除角色

### 权限管理
- `GET /api/permissions` - 获取权限列表
- `POST /api/permissions` - 创建权限
- `PUT /api/permissions/{id}` - 更新权限
- `DELETE /api/permissions/{id}` - 删除权限

### 菜单管理
- `GET /api/menus/tree` - 获取菜单树
- `POST /api/menus` - 创建菜单
- `PUT /api/menus/{id}` - 更新菜单
- `DELETE /api/menus/{id}` - 删除菜单

### 商品管理
- `GET /api/products` - 获取商品列表
- `GET /api/products/{id}` - 获取商品详情
- `GET /api/products/search?keyword=xxx` - 搜索商品
- `GET /api/products/category/{category}` - 按分类获取商品
- `POST /api/products` - 发布商品
- `PUT /api/products/{id}` - 更新商品
- `DELETE /api/products/{id}` - 删除商品

### 购物车
- `GET /api/cart` - 获取购物车
- `POST /api/cart/add` - 添加到购物车
- `PUT /api/cart/{id}` - 更新购物车项
- `DELETE /api/cart/{id}` - 删除购物车项

### 订单管理
- `GET /api/orders` - 获取订单列表
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders` - 创建订单
- `PUT /api/orders/{id}/cancel` - 取消订单

### 支付管理
- `POST /api/payments` - 创建支付
- `POST /api/payments/process` - 处理支付
- `GET /api/payments/{paymentNo}` - 获取支付详情

## 消息队列

系统使用 RabbitMQ 处理异步任务：

- **订单队列**: 订单创建后发送消息
- **支付队列**: 支付成功后发送消息

## 缓存策略

- 商品信息缓存：1小时
- 商品列表缓存：30分钟
- 购物车缓存：30分钟

## 开发说明

### 后端开发
1. 使用 JPA 进行数据持久化
2. 使用 Redis 进行缓存
3. 使用 RabbitMQ 进行异步消息处理
4. 使用 JWT 进行身份认证

### 前端开发
1. 使用 Vue 3 Composition API
2. 使用 Element Plus 组件库
3. 使用 Vuex 进行状态管理
4. 使用 Axios 进行 HTTP 请求

## 许可证

MIT License

