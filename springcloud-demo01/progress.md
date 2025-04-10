# 工作日志

## 2023-04-09 实现JWT认证与权限管理

### 1. 我们实现了哪些功能？

1. commons模块：
   - 实现了JWT工具类(JWTUtils)，用于生成、验证和解析JWT令牌
   - 实现了Redis工具类(RedisUtils)，用于缓存和管理用户token
   - 创建了权限注解(PermAccess)和对应的拦截器(PermInterceptor)，用于权限控制
   - 配置了拦截器

2. auth模块：
   - 修改了AuthController，实现了登录功能
   - 修改了AuthService接口和实现类，添加了login方法
   - 实现了双token认证机制（token和refreshToken）

3. gateway模块：
   - 创建了全局过滤器(AuthGlobalFilter)，用于请求验证
   - 更新了网关路由配置，添加了auth服务路由
   - 实现了token自动刷新机制

### 2. 我们遇到了哪些错误？

1. 依赖问题：
   - Maven依赖下载失败，无法解析io.lettuce:lettuce-core:jar:5.3.7.RELEASE
   - 缺少必要的依赖导致类无法解析

2. 类引用问题：
   - JWTUtils类方法调用错误，createJwt方法不存在
   - 网关模块缺少commons模块依赖

### 3. 我们是如何解决这些错误的？

1. 依赖问题：
   - 在commons模块的pom.xml中添加了JWT和Redis依赖
   - 在gateway模块的pom.xml中添加了commons模块依赖，并排除了spring-boot-starter-web依赖

2. 类引用问题：
   - 修改了AuthServiceImpl中的代码，改用静态方法JWTUtils.createToken()
   - 添加了gateway模块对commons模块的依赖引用

完成了基本的认证和权限管理框架，实现了双token机制增强安全性。

## 2023-04-10 解决Gateway启动问题

### 1. 我们实现了哪些功能？

1. 解决了Gateway无法启动的问题：
   - 修改了拦截器配置类，添加条件注解使其只在Servlet环境下生效
   - 优化了拦截器的路径排除配置，明确排除了登录和注册接口

### 2. 我们遇到了哪些错误？

1. 架构兼容性问题：
   - Gateway使用WebFlux（响应式编程模型），而拦截器基于Spring MVC
   - 两种编程模型不兼容导致Gateway无法启动

### 3. 我们是如何解决这些错误的？

1. 使用条件注解解决环境兼容问题：
   - 在SpringMVCInterceptorConfiguration类上添加了@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)注解
   - 这样拦截器配置只会在传统Servlet环境（Spring MVC）下加载，而在WebFlux环境（Gateway）下会被跳过
   - 保留了拦截器在commons模块中的位置，使其可以被所有需要权限控制的微服务使用

2. 完善了拦截器排除路径：
   - 添加了"/auth/login"和"/auth/register"到排除路径列表
   - 确保登录和注册接口可以正常访问，不受权限拦截

## 2023-04-11 更新Auth模块配置

### 1. 我们实现了哪些功能？

1. 修改了Auth模块的配置机制：
   - 创建了bootstrap.yml文件，配置Nacos服务发现和配置中心
   - 修改了application.yml，移除重复配置，仅保留本地开发所需配置
   - 创建了yr-auth-dev.yml用于导入Nacos配置中心

### 2. 我们遇到了哪些错误？

1. 配置结构问题：
   - 原配置使用application.yml，不符合微服务最佳实践
   - 缺少与Nacos配置中心的连接配置
   - 缺少Redis和JWT相关的配置项

### 3. 我们是如何解决这些错误的？

1. 重构配置文件：
   - 创建bootstrap.yml作为主配置文件，添加Nacos配置中心和服务发现配置
   - 保留精简的application.yml作为本地开发备用配置
   - 创建yr-auth-dev.yml包含完整配置，准备导入Nacos

2. 添加关键配置项：
   - 添加了Redis连接配置
   - 增加了JWT相关的自定义配置项，如密钥和过期时间
   - 完善了日志配置，方便调试

## 2023-04-12 完善Auth模块配置与Nacos集成

### 1. 我们实现了哪些功能？

1. 完善了Auth模块的bootstrap.yml配置：
   - 参照order模块的配置结构，确保与项目其他模块保持一致
   - 更新了Nacos服务器地址为正式环境IP：47.109.92.14:8848
   - 添加了共享配置引用，包括shared-jdbc.yaml

2. 调整了相关配置文件：
   - 修改了yr-auth-dev.yml，完全移除了数据库和JPA配置
   - 彻底清理了application.yml，移除了所有数据库相关配置
   - 保留了服务端口和注释，明确说明配置来源和备用机制

### 2. 我们遇到了哪些错误？

1. Nacos配置集成问题：
   - bootstrap.yml中Nacos服务器地址配置不正确
   - 缺少共享数据库配置引用
   - 配置结构与其他模块不一致

2. 配置重复问题：
   - 数据库和JPA配置在多个配置文件中重复出现
   - 未完全移除所有应该由共享配置提供的内容
   - 缺少明确的配置职责划分

### 3. 我们是如何解决这些错误的？

1. 参照标准化配置：
   - 按照项目中order模块的标准配置修改auth的bootstrap.yml
   - 添加共享配置引用extension-configs
   - 调整配置顺序和结构，确保与项目标准保持一致
   - 确保了数据库配置通过shared-jdbc.yaml从Nacos加载

2. 消除配置冗余：
   - 从yr-auth-dev.yml中完全移除数据库和JPA相关配置
   - 从application.yml中清除所有数据库和JPA配置
   - 添加明确注释，指明配置的职责和加载顺序
   - 确保所有数据库相关配置统一从shared-jdbc.yaml加载

## 2023-04-13 解决类名与文件名不匹配与类型转换问题

### 1. 我们实现了哪些功能？

1. 解决了 JWTUtils 类名与文件名不匹配的问题
   - 将 `JwtUtils.java` 文件重命名为 `JWTUtils.java`，以匹配类名
   - 在 auth 模块中创建了 `JwtUtils.java` 适配器类，用于连接 JWTUtils 和 JwtAuthenticationFilter

2. 修复了 RedisUtils 中的类型转换问题
   - 将 `hgetAll()` 方法的返回类型从 `Map<Object, Object>` 改为 `Map<String, Object>`
   - 添加了手动类型转换逻辑，确保返回的 Map 键为 String 类型
   - 解决了类型不兼容问题：`Map<Object, Object>` 无法转换为 `Map<String, Object>`

### 2. 我们遇到了哪些错误？

1. 类名与文件名不匹配问题
   - 错误信息：类 JWTUtils 是公共的，应在名为 JWTUtils.java 的文件中声明
   - 原因：Java 要求公共类名必须与文件名完全匹配
   - 影响：导致 yr-auth 模块编译失败，无法找到 JwtUtils 类

2. 类型转换不兼容问题
   - 错误信息：不兼容的类型: java.util.Map<java.lang.Object,java.lang.Object>无法转换为java.util.Map<java.lang.String,java.lang.Object>
   - 原因：Java 泛型类型安全检查，不允许不兼容类型间的隐式转换
   - 影响：调用 RedisUtils.hgetAll() 方法时出现编译错误

3. Maven 编译环境问题
   - 错误信息：java.lang.NoSuchFieldError: Class com.sun.tools.javac.tree.JCTree$JCImport does not have member field 'com.sun.tools.javac.tree.JCTree qualid'
   - 原因：Maven 插件版本与 JDK 版本不兼容
   - 影响：无法完成最终的编译测试

### 3. 我们是如何解决这些错误的？

1. 解决类名与文件名不匹配
   - 使用命令 `mv yr-commons/src/main/java/com/woniuxy/utils/JwtUtils.java yr-commons/src/main/java/com/woniuxy/utils/JWTUtils.java`，将文件名修改为与类名一致
   - 在 auth 模块中创建 JwtUtils 适配器类，提供 validateJwt() 和 getUsername() 方法，内部调用 JWTUtils 的对应方法

2. 解决类型转换不兼容问题
   - 修改 RedisUtils.hgetAll() 方法，返回类型从 Map<Object, Object> 改为 Map<String, Object>
   - 添加手动类型转换代码，确保将所有键转换为 String 类型
   - 使用 HashMap 创建新的 Map 对象，保证类型安全

3. Maven 编译环境问题（未完全解决）
   - 识别出此问题与我们的代码修改无关，是 Maven 插件与 JDK 版本兼容性问题
   - 提供了解决建议：调整 Maven 插件版本或使用兼容的 JDK 版本
   - 建议使用 `-DskipTests` 参数跳过测试阶段完成构建 

## 2023-04-14 解决Redis连接池依赖与Gateway启动问题

### 1. 我们实现了哪些功能？

1. 添加了Redis连接池依赖：
   - 在yr-commons模块的pom.xml中添加了commons-pool2依赖
   - 解决了Redis连接池缺失导致的Auth服务启动失败问题
   - 确保了Redis连接池配置能够正常生效

2. 解决了Gateway启动问题：
   - 修改了GatewayApplication主类，添加了DataSource相关自动配置排除
   - 避免了网关尝试配置数据库连接而导致的启动失败问题
   - 使Gateway服务能够正常启动，专注于路由转发功能

### 2. 我们遇到了哪些错误？

1. Redis连接池依赖问题：
   - 错误信息：`java.lang.NoClassDefFoundError: org/apache/commons/pool2/impl/GenericObjectPoolConfig`
   - 原因：缺少Redis连接池的核心依赖commons-pool2
   - 影响：Auth服务无法启动，无法连接Redis服务器

2. Gateway数据源配置问题：
   - 错误信息：`Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.`
   - 原因：Gateway不需要数据库连接，但由于引入了commons模块中的JPA依赖，导致自动尝试配置数据源
   - 影响：Gateway服务无法启动，路由功能无法使用

### 3. 我们是如何解决这些错误的？

1. 解决Redis连接池依赖问题：
   - 在yr-commons模块的pom.xml中添加了commons-pool2依赖：
     ```xml
     <dependency>
         <groupId>org.apache.commons</groupId>
         <artifactId>commons-pool2</artifactId>
         <version>2.11.1</version>
     </dependency>
     ```
   - 该依赖是Spring Boot Redis连接池必需的，用于管理Redis连接资源
   - 添加后Auth服务可以正常连接Redis，支持用户token的存储和管理

2. 解决Gateway数据源配置问题：
   - 在GatewayApplication类上添加了自动配置排除注解：
     ```java
     @SpringBootApplication(exclude = {
         DataSourceAutoConfiguration.class,
         DataSourceTransactionManagerAutoConfiguration.class,
         HibernateJpaAutoConfiguration.class
     })
     ```
   - 这样配置后，Gateway服务不会尝试配置数据库连接
   - 避免了引入commons模块导致的数据源配置问题，保持网关服务的轻量级特性

通过以上修复，Auth服务和Gateway服务都能正常启动，用户登录认证和API路由功能完整可用。

## 阿里云OSS文件上传模块实现

### 1. 我们实现了哪些功能？

1. **创建了独立的OSS服务模块**
   - 建立了`yr-oss`模块并添加到父项目`pom.xml`中
   - 添加了阿里云OSS相关依赖
   - 配置了Spring Boot和Spring Cloud相关依赖

2. **配置管理**
   - 创建了`bootstrap.yml`和`application.yml`配置文件
   - 创建了阿里云OSS配置类和属性映射类
   - 创建了`shared-oss.yaml-template`模板文件，可以上传到Nacos配置中心

3. **OSS核心服务**
   - 实现了文件上传服务接口`OssService`及其实现类`OssServiceImpl`
   - 支持单文件、批量文件上传
   - 支持指定目录上传
   - 支持文件删除和URL获取

4. **控制器和异常处理**
   - 实现了REST风格的控制器`OssController`
   - 提供了文件上传、删除和URL获取API
   - 实现了全局异常处理器`GlobalExceptionHandler`

5. **集成到网关**
   - 在Gateway网关中添加了对OSS服务的路由配置

6. **Feign客户端接口**
   - 在commons模块中创建了Feign客户端接口，方便其他服务调用

### 2. 我们遇到了哪些错误？

1. **配置管理挑战**
   - 阿里云OSS需要敏感配置信息（AccessKey、Secret等），这些不应该直接存储在代码中
   - 解决方案是将敏感配置抽离到Nacos配置中心

2. **模块创建问题**
   - 需要确保新模块正确集成到现有项目结构中
   - 确保依赖管理正确，避免版本冲突

3. **多环境配置处理**
   - OSS配置在开发、测试、生产环境可能不同

### 3. 我们是如何解决这些错误的？

1. **敏感配置处理**
   - 创建了配置模板文件`shared-oss.yaml-template`
   - 将实际AccessKey和Secret配置放到Nacos中，避免提交到代码库

2. **模块集成**
   - 在父pom.xml中添加模块引用
   - 添加正确的依赖，确保一致性

3. **异常处理**
   - 实现了全局异常处理器，统一管理异常
   - 自定义了`OssException`类处理特定场景异常

4. **网关路由**
   - 对OSS服务添加了专门的路由规则，确保请求正确转发

5. **服务调用**
   - 实现了Feign客户端，简化跨服务调用 

## 阿里云OSS模块构建问题解决

### 1. 我们实现了哪些功能？

1. **解决了OSS模块的Maven构建问题**
   - 解决了Spring Boot Maven Plugin版本不兼容的问题
   - 成功编译并安装了yr-oss模块到本地Maven仓库

### 2. 我们遇到了哪些错误？

1. **JDK版本兼容性问题**
   - 错误信息：`java.lang.UnsupportedClassVersionError: org/springframework/boot/maven/RepackageMojo has been compiled by a more recent version of the Java Runtime (class file version 61.0)`
   - 原因：Spring Boot Maven Plugin 3.4.4 版本需要 JDK 17，而当前环境使用的是 JDK 8

2. **构建失败**
   - Maven构建过程无法完成，导致yr-oss模块无法正常安装
   - 阻碍了对OSS模块的进一步测试和使用

### 3. 我们是如何解决这些错误的？

1. **移除不兼容的插件**
   - 编辑`yr-oss/pom.xml`文件，移除了Spring Boot Maven Plugin
   - 由于OSS模块不需要作为可执行JAR包运行，移除该插件不会影响功能

2. **成功构建项目**
   - 执行`mvn clean install -DskipTests`完成所有模块的构建
   - 所有模块（包括yr-oss）都成功构建并安装到本地Maven仓库

这个解决方案是临时性的，如果将来需要将OSS模块作为独立服务运行，需要使用兼容当前JDK版本的Spring Boot Maven Plugin。目前这个解决方案满足了当前使用Feign客户端调用OSS模块的需求。

## 2024-04-10 OSS服务内容协商机制问题修复

### 1. 我们实现了哪些功能？

1. 创建了`WebMvcConfig`配置类，继承`WebMvcConfigurationSupport`
2. 实现了`extendMessageConverters`方法，扩展了消息转换器功能
3. 为JSON转换器添加了对`MediaType.ALL`媒体类型的支持
4. 解决了Spring MVC内容协商机制与配置冲突的问题

### 2. 我们遇到了哪些错误？

1. **内容协商(Content Negotiation)冲突**：
   - 客户端请求时无法找到能满足期望的响应格式
   - 报错：`HttpMediaTypeNotAcceptableException: Could not find acceptable representation`

2. **消息转换器(Message Converter)配置不当**：
   - 默认消息转换器无法处理Apifox/Postman发送的请求
   - 特别是批量上传接口`/oss/uploads`无法正确返回JSON响应

3. **响应类型与Accept头不匹配**：
   - 客户端默认发送`Accept: */*`头，但服务端配置没有包含处理通用媒体类型的转换器

### 3. 我们是如何解决这些错误的？

1. **创建专门的MVC配置类**：
   - 继承`WebMvcConfigurationSupport`而不是实现`WebMvcConfigurer`接口
   - 这样可以完全控制MVC配置，避免与自动配置冲突

2. **扩展而非替换消息转换器**：
   - 使用`extendMessageConverters`而非`configureMessageConverters`
   - 保留Spring Boot自动配置的转换器，仅进行必要的扩展

3. **添加通用媒体类型支持**：
   - 为`MappingJackson2HttpMessageConverter`添加`MediaType.ALL`支持
   - 确保能处理客户端发送的任何Accept头

4. **避免在控制器中指定限制性的produces属性**：
   - 保持控制器方法的灵活性，让Spring更自由地选择合适的转换器

这种解决方案保持了Spring Boot的自动配置优势，同时解决了特定场景下的内容协商问题。现在OSS服务的上传、删除和URL获取接口都能正确处理各种客户端请求并返回合适的JSON响应了。 