# API接口文档

## 项目说明

本项目是基于Spring Cloud Alibaba构建的微服务应用，包含多个模块：
- yr-auth：认证授权服务
- yr-product：产品服务
- yr-order：订单服务
- yr-gateway：API网关服务

所有请求通过网关（Gateway）进行路由和过滤，实现了JWT令牌认证和基于角色的权限控制。

## 目录

1. [认证服务](#1-认证服务)
   - [1.1 用户登录](#11-用户登录)
   - [1.2 用户注册](#12-用户注册)
2. [产品服务](#2-产品服务)
   - [2.1 获取所有产品](#21-获取所有产品)
   - [2.2 根据ID获取产品](#22-根据id获取产品)
   - [2.3 修改产品库存](#23-修改产品库存)
   - [2.4 删除产品](#24-删除产品)
   - [2.5 条件查询产品](#25-条件查询产品)
   - [2.6 根据库存查询产品](#26-根据库存查询产品)
   - [2.7 根据库存范围查询产品](#27-根据库存范围查询产品)
3. [订单服务](#3-订单服务)
   - [3.1 创建订单](#31-创建订单)

## 完整请求访问路径

所有接口均通过API网关访问，完整的请求URL格式如下：

### 网关基础URL

- **开发环境**: `http://localhost:8889`
- **测试环境**: `http://test-api.example.com`
- **生产环境**: `http://api.example.com`

### 完整请求URL示例

| 服务 | 相对路径 | 完整请求URL (开发环境) |
|------|---------|----------------------|
| 认证服务 | `/auth/login` | `http://localhost:8889/auth/login` |
| 认证服务 | `/auth/register` | `http://localhost:8889/auth/register` |
| 产品服务 | `/product/` | `http://localhost:8889/product/` |
| 产品服务 | `/product/{pid}` | `http://localhost:8889/product/1` |
| 产品服务 | `/product/{pid}/{number}` | `http://localhost:8889/product/1/10` |
| 产品服务 | `/product/delete/{pid}` | `http://localhost:8889/product/delete/1` |
| 产品服务 | `/product/findOne` | `http://localhost:8889/product/findOne?name=产品&price=1000&stock=100` |
| 产品服务 | `/product/query-by-stock` | `http://localhost:8889/product/query-by-stock?stock=50` |
| 产品服务 | `/product/query-by-stock/range` | `http://localhost:8889/product/query-by-stock/range?minStock=10&maxStock=100` |
| 订单服务 | `/order/` | `http://localhost:8889/order/` |

**注意**:
- 在URL中的路径参数（如`{pid}`）需要替换为实际的值
- 查询参数通过`?`符号添加到URL后面，多个参数通过`&`连接
- 所有非登录/注册接口都需要在请求头中添加Authorization参数

## 1. 认证服务

### 1.1 用户登录

- **接口URL**: `/auth/login`
- **请求方式**: POST
- **接口描述**: 用户登录认证，成功后返回刷新令牌(refreshToken)，使用双token认证机制

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码"
}
```

**响应结果**:
- 登录成功:
```json
{
  "code": 200,
  "msg": "success",
  "data": "刷新令牌(refreshToken)"
}
```
- 登录失败:
```json
{
  "code": 500,
  "msg": "账号或密码错误"
}
```

**特别说明**:
- 使用双token认证机制，返回的是refreshToken，而不是JWT token
- refreshToken有效期为60分钟，JWT token有效期为10分钟
- JWT token存储在Redis中，与refreshToken关联
- 当JWT token过期但refreshToken未过期时，系统会自动生成新的JWT token

### 1.2 用户注册

- **接口URL**: `/auth/register`
- **请求方式**: POST
- **接口描述**: 用户注册功能

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码",
  "email": "邮箱"
}
```

**响应结果**:
```json
{
  "code": 200,
  "msg": "注册成功"
}
```

## 2. 产品服务

所有产品接口的请求都需要在请求头中携带Authorization参数，值为登录接口返回的refreshToken。

### 2.1 获取所有产品

- **接口URL**: `/product/`
- **请求方式**: GET
- **接口描述**: 获取所有产品列表

**请求参数**: 无

**响应结果**:
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "pid": 1,
      "name": "产品名称",
      "price": 1000.0,
      "stock": 100
    }
  ],
  "product_name": "产品名称配置值"
}
```

### 2.2 根据ID获取产品

- **接口URL**: `/product/{pid}`
- **请求方式**: GET
- **接口描述**: 根据产品ID获取产品详情

**请求参数**:
- pid: 产品ID (路径参数)

**响应结果**:
```json
{
  "pid": 1,
  "name": "产品名称",
  "price": 1000.0,
  "stock": 100
}
```

**特别说明**:
- 每4次请求会模拟异常，用于测试服务降级功能

### 2.3 修改产品库存

- **接口URL**: `/product/{pid}/{number}`
- **请求方式**: POST
- **接口描述**: 根据产品ID和数量修改库存

**请求参数**:
- pid: 产品ID (路径参数)
- number: 要减少的库存数量 (路径参数)

**响应结果**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "pid": 1,
    "name": "产品名称",
    "price": 1000.0,
    "stock": 90
  },
  "msg": "yr-product:8081"
}
```

### 2.4 删除产品

- **接口URL**: `/product/delete/{pid}`
- **请求方式**: DELETE
- **接口描述**: 根据产品ID删除产品

**请求参数**:
- pid: 产品ID (路径参数)

**响应结果**:
```json
{
  "code": 200,
  "msg": "success"
}
```

### 2.5 条件查询产品

- **接口URL**: `/product/findOne`
- **请求方式**: GET
- **接口描述**: 根据条件查询产品

**请求参数**:
- name: 产品名称 (查询参数)
- price: 产品价格 (查询参数)
- stock: 库存数量 (查询参数)

**响应结果**:
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "pid": 1,
      "name": "产品名称",
      "price": 1000.0,
      "stock": 100
    }
  ]
}
```

### 2.6 根据库存查询产品

- **接口URL**: `/product/query-by-stock`
- **请求方式**: GET
- **接口描述**: 根据库存查询产品，用于测试库存断言

**请求参数**:
- stock: 库存数量 (查询参数，可选)

**响应结果**:
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "pid": 1,
      "name": "产品名称",
      "price": 1000.0,
      "stock": 100
    }
  ],
  "stockValue": 100,
  "serviceInfo": "yr-product:8081",
  "product_name": "产品名称配置值"
}
```

### 2.7 根据库存范围查询产品

- **接口URL**: `/product/query-by-stock/range`
- **请求方式**: GET
- **接口描述**: 根据库存范围查询产品

**请求参数**:
- minStock: 最小库存 (查询参数，默认值: 0)
- maxStock: 最大库存 (查询参数，默认值: 10000)

**响应结果**:
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "pid": 1,
      "name": "产品名称",
      "price": 1000.0,
      "stock": 100
    }
  ],
  "stockRange": "0-10000",
  "serviceInfo": "yr-product:8081",
  "product_name": "产品名称配置值"
}
```

## 3. 订单服务

所有订单接口的请求都需要在请求头中携带Authorization参数，值为登录接口返回的refreshToken。

### 3.1 创建订单

- **接口URL**: `/order/`
- **请求方式**: POST
- **接口描述**: 创建新订单，包含Sentinel熔断器保护

**请求参数**:
```json
{
  "pid": 1,
  "number": 2,
  "price": 2000.0
}
```

**响应结果**:
- 正常响应:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "oid": 1,
    "pid": 1,
    "number": 2,
    "price": 2000.0
  }
}
```
- 熔断响应:
```json
{
  "code": 200,
  "msg": "方法熔断，服务器正在维护中..."
}
```

**特别说明**:
- 接口使用Sentinel实现熔断保护，当服务负载过高或出现异常时会触发熔断
- 创建订单时会通过Feign调用产品服务修改库存

## 认证与授权

所有接口(除了登录和注册)都需要在请求头中携带Authorization参数，用于验证用户身份。

**请求头格式**:
```
Authorization: refreshToken值
```

**完整请求示例**:
```bash
# 使用curl发送请求示例
curl -X GET "http://localhost:8889/product/" \
  -H "Authorization: 5f8d7e6c-9b4a-3f2e-8d1c-7b6a5f4d3e2c" \
  -H "Content-Type: application/json"

# 使用Postman或其他HTTP客户端
GET http://localhost:8889/product/
Headers:
  Authorization: 5f8d7e6c-9b4a-3f2e-8d1c-7b6a5f4d3e2c
  Content-Type: application/json
```

授权流程:
1. 客户端通过登录接口获取refreshToken
2. 将refreshToken放在请求头中，发送API请求
3. Gateway全局过滤器验证token有效性
4. 如果JWT token过期但refreshToken未过期，会自动生成新的JWT token
5. 如果refreshToken过期，返回401错误，需要重新登录

## 错误码说明

- 200: 成功
- 401: 未授权，token无效或已过期
- 403: 权限不足
- 500: 服务器内部错误 