# AI聊天应用（AI Model Chat）

## 项目概述

AI聊天应用是一个基于Spring Boot开发的Web应用，集成了多种大型语言模型（LLM）的聊天界面，支持流式输出和思考过程展示。目前已集成DeepSeek Chat、DeepSeek Reasoner和Claude系列模型，提供了统一的用户界面和API接口。

## 技术架构

### 后端技术栈

- **核心框架**：Spring Boot 3.2.0
- **模板引擎**：Thymeleaf
- **HTTP客户端**：OkHttp 4.10.0
- **JSON处理**：org.json 20231013
- **开发工具**：Lombok 1.18.26

### 前端技术栈

- **JavaScript库**：jQuery 3.6.0
- **Markdown渲染**：Marked.js
- **代码高亮**：Highlight.js
- **响应式设计**：自适应CSS布局

### 系统架构

```
com.aimodel.chat
├── controller      # 控制器层，处理HTTP请求
├── service         # 服务层，实现业务逻辑
│   └── impl        # 服务实现类
├── model           # 数据模型，定义DTO对象
├── util            # 工具类
├── exception       # 异常处理
└── Application.java # 应用入口
```

## 功能特性

### 主要功能

1. **多模型支持**
   - DeepSeek Chat模型
   - DeepSeek Reasoner模型（带思考过程）
   - Claude系列模型（Haiku, Sonnet, Opus）

2. **流式输出**
   - 实时展示AI回复内容
   - 支持Server-Sent Events (SSE)技术

3. **思考过程展示**
   - 对支持思考过程的模型（如DeepSeek Reasoner）可展示思考过程
   - 可折叠/展开思考过程

4. **富文本展示**
   - 支持Markdown格式渲染
   - 代码语法高亮
   - 数学公式渲染

5. **响应式设计**
   - 适配不同尺寸的屏幕和设备
   - 良好的移动端体验

## 安装部署

### 环境要求

- JDK 17+
- Maven 3.6+
- 现代浏览器（Chrome, Firefox, Edge等）

### 构建步骤

1. **克隆代码仓库**

```bash
git clone https://github.com/yourusername/ai-model-chat.git
cd ai-model-chat
```

2. **修改配置文件**

编辑`src/main/resources/application.yml`文件，配置您的AI模型API密钥：

```yaml
ai:
  model:
    deepseek:
      r1:
        api-key: your-deepseek-api-key
      v3:
        api-key: your-deepseek-api-key
    claude:
      api-key: your-claude-api-key
```

3. **构建应用**

```bash
mvn clean package
```

4. **运行应用**

```bash
java -jar target/chat-0.0.1-SNAPSHOT.jar
```

5. **访问应用**

打开浏览器，访问：`http://localhost:8080/ai-chat`

## API接口文档

### 聊天接口

#### 1. 发送消息（同步）

- **URL**: `/api/chat`
- **方法**: POST
- **请求参数**:
  - `message`: 用户消息内容
  - `modelType`: 模型类型，可选值：`deepseek-v3`, `deepseek-r1`, `claude-3-haiku`, `claude-3-opus`, `claude-3-sonnet`, `claude-3.5-sonnet`
- **响应示例**:
```json
{
  "content": "AI回复的内容",
  "reasoningContent": "思考过程（如果有）",
  "tokensIn": 10,
  "tokensOut": 100,
  "model": "DeepSeek Reasoner",
  "provider": "Deepseek"
}
```

#### 2. 发送消息（流式）

- **URL**: `/api/chat/stream`
- **方法**: GET
- **请求参数**:
  - `message`: 用户消息内容
  - `modelType`: 模型类型
- **响应类型**: Server-Sent Events
- **事件类型**:
  - `reasoning`: 思考过程
  - `phaseChange`: 阶段变化（从思考到回答）
  - `content`: 回复内容
  - `complete`: 完成事件，包含完整响应数据
  - `error`: 错误事件

## 使用指南

### 基本使用

1. 访问应用主页
2. 在底部输入框中输入您的问题
3. 点击发送按钮或按回车键发送消息
4. 查看AI实时生成的回复

### 切换模型

1. 点击输入框左侧的模型选择按钮
2. 从下拉菜单中选择所需的AI模型
3. 系统会显示模型已切换的提示

### 查看思考过程

1. 使用DeepSeek Reasoner模型时，回复中会包含"显示思考过程"按钮
2. 点击该按钮可展开/折叠思考过程

## 开发指南

### 添加新模型

要添加新的AI模型，需要以下步骤：

1. 创建新的`ModelService`实现类
2. 在`AIChatServiceImpl`中注册新模型
3. 在前端界面添加模型选项

### 贡献代码

1. Fork本项目仓库
2. 创建您的特性分支：`git checkout -b feature/your-feature`
3. 提交您的更改：`git commit -m 'Add some feature'`
4. 推送到分支：`git push origin feature/your-feature`
5. 提交Pull Request

## 许可证

本项目基于MIT许可证开源。

## 联系方式

如有问题或建议，请通过以下方式联系我们：

- Email: your-email@example.com
- GitHub Issues: [提交Issue](https://github.com/yourusername/ai-model-chat/issues) 