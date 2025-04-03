# AI聊天应用

这是一个基于SpringBoot和AI大模型的聊天应用，支持与多种AI模型进行对话。

## 功能特点

- 支持多种AI大模型（Deepseek R1、Deepseek V3、Claude）
- 简洁美观的聊天界面
- 实时消息交互
- 完整的会话管理

## 技术栈

- **后端**：SpringBoot 3.2.0
- **前端**：HTML/CSS/JavaScript
- **数据库**：MySQL
- **模板引擎**：Thymeleaf
- **会话管理**：Spring Session
- **HTTP客户端**：OkHttp3

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- 相关AI模型API（本地或远程）

### 配置

1. 克隆项目到本地
2. 修改`src/main/resources/application.yml`配置文件：
   - 数据库连接信息
   - AI模型API地址和密钥

### 运行

```bash
mvn spring-boot:run
```

应用将在[http://localhost:8080/ai-chat](http://localhost:8080/ai-chat)运行。

## 项目结构

```
src/
 ├── main/
 │   ├── java/
 │   │   └── com/aimodel/chat/
 │   │       ├── Application.java (主入口)
 │   │       ├── config/         (配置类)
 │   │       ├── controller/     (控制器)
 │   │       ├── service/        (服务层)
 │   │       ├── repository/     (数据访问层)
 │   │       ├── model/          (数据模型)
 │   │       ├── exception/      (异常处理)
 │   │       └── util/           (工具类)
 │   └── resources/
 │       ├── application.yml    (应用配置)
 │       ├── static/           (静态资源)
 │       └── templates/        (HTML模板)
 └── test/                     (测试代码)
```

## API接口

### 聊天接口

- **URL**：`/api/chat`
- **方法**：`POST`
- **请求体**：
  ```json
  {
    "message": "用户消息内容",
    "modelType": "deepseek-r1"  // 可选值: deepseek-r1, deepseek-v3, claude
  }
  ```
- **响应**：
  ```json
  {
    "content": "AI回复内容",
    "model": "模型名称",
    "provider": "模型提供商",
    "tokensIn": 123,
    "tokensOut": 456
  }
  ```

## 支持的模型

- **Deepseek R1**：通过Ollama API使用
- **Deepseek V3**：通过Ollama API使用
- **Claude**：通过Anthropic API使用

## 许可证

MIT 