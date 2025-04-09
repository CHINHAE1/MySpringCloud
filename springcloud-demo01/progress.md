# 工作进度记录

## 1. 我们实现了哪些功能？

1. **创建JWT工具类**：
   - 在`yr-commons`模块中创建了`JwtUtils`类，提供了JWT的生成、解析和验证功能。
   - 实现了各种辅助方法，如获取用户ID、获取用户名、检查令牌是否过期等。

2. **实现自定义登录校验过滤器**：
   - 在`yr-auth`模块中创建了`JwtAuthenticationFilter`过滤器。
   - 该过滤器继承自`OncePerRequestFilter`，确保在一次请求中只执行一次。
   - 实现了从请求头提取JWT令牌、验证其有效性并设置Security上下文的功能。

3. **配置Spring Security**：
   - 创建了`SecurityConfig`配置类，配置了安全规则和过滤器链。
   - 添加了`AuthenticationManager`的Bean配置，用于处理认证请求。
   - 配置了URL访问权限，允许登录和注册接口匿名访问，其他接口需要认证。

4. **实现认证服务**：
   - 创建了`AuthService`接口和`AuthServiceImpl`实现类，处理用户认证逻辑。
   - 使用Spring Security的认证机制验证用户凭据，并在认证成功后生成JWT令牌。

5. **实现用户详情服务**：
   - 创建了`UserDetailsServiceImpl`实现类，模拟用户数据的加载。
   - 为测试提供了一些预定义用户（admin和user）。

6. **创建数据模型**：
   - 创建了`LoginRequest`类，用于接收登录请求数据。
   - 创建了`LoginResponse`类，用于返回登录结果和JWT令牌。

7. **创建认证控制器**：
   - 实现了`AuthController`，提供了登录接口。
   - 日志记录用户登录请求和结果。

## 2. 我们遇到了哪些错误？

1. **依赖缺失问题**：
   - 在创建`LoginRequest`类时，缺少JSR-303 Bean Validation依赖，导致`@NotBlank`注解无法解析。
   - `AuthController`引用的`AuthService`接口尚未创建，导致编译错误。

2. **组件依赖问题**：
   - `SecurityConfig`配置类需要`UserDetailsService`的实现，初始未提供。
   - `AuthServiceImpl`需要`AuthenticationManager`，但最初未在`SecurityConfig`中配置Bean。

## 3. 我们是如何解决这些错误的？

1. **依赖缺失问题解决**：
   - 在`yr-auth`模块的`pom.xml`中添加了`spring-boot-starter-validation`依赖，解决了Bean Validation注解的问题。
   - 创建了`AuthService`接口和实现类，解决了编译错误。

2. **组件依赖问题解决**：
   - 创建了`UserDetailsServiceImpl`类实现`UserDetailsService`接口，提供用户数据加载功能。
   - 在`SecurityConfig`中添加了`@Bean`注解的`authenticationManagerBean`方法，暴露`AuthenticationManager`供其他组件使用。
   - 在`SecurityConfig`中注入了`UserDetailsService`，并在`configure(AuthenticationManagerBuilder)`方法中进行配置。

3. **结构组织问题解决**：
   - 按照SpringBoot最佳实践，将代码组织到了合适的包结构中。
   - 为JWT工具类和登录校验过滤器添加了详细的注释，解释了其功能和使用方法。 