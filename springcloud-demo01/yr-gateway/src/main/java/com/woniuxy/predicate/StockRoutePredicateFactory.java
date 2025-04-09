package com.woniuxy.predicate; // 你可以根据项目结构调整包名

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

// 注意：根据你的Spring Boot版本，可能需要使用 javax.validation.constraints.NotNull
// 如果你使用的是 Spring Boot 2.x, 使用下面的导入
import javax.validation.constraints.NotNull;
// 如果你使用的是 Spring Boot 3.x 或更高版本, 使用下面的导入
// import jakarta.validation.constraints.NotNull; 

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 自定义库存路由断言工厂
 * <p>
 * 用于根据请求参数中的 'stock' 值来判断是否匹配路由。
 * 需要在 application.yml 中配置 minStock 和 maxStock。
 * </p>
 * <p>
 * 示例配置:
 * predicates:
 *   - Stock=1, 5000  # 匹配 stock 在 1 到 5000 之间的请求
 *   - Stock=5001, 10000 # 匹配 stock 在 5001 到 10000 之间的请求
 * </p>
 */
@Component // 注册为Spring Bean，让Gateway能够发现它
public class StockRoutePredicateFactory extends AbstractRoutePredicateFactory<StockRoutePredicateFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(StockRoutePredicateFactory.class);

    // 定义配置类中参数的顺序，对应 YAML 中值的顺序 (minStock, maxStock)
    private static final List<String> LST_CONFIG_FILED = Arrays.asList("minStock", "maxStock");

    /**
     * 构造函数，指定配置类
     */
    public StockRoutePredicateFactory() {
        super(Config.class);
    }


    /**
     * 返回配置参数的顺序
     * 用于支持配置文件中的快捷写法，例如： - Stock=1, 5000
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return LST_CONFIG_FILED;
    }

    /**
     * 核心逻辑：应用断言规则
     *
     * @param config 配置对象，包含 minStock 和 maxStock
     * @return Predicate<ServerWebExchange> 断言函数
     */
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return (GatewayPredicate) exchange -> {
            // 1. 从请求中获取 'stock' 查询参数
            //    我们假设库存值通过名为 'stock' 的查询参数传递，例如 /some/path?stock=100
            String stockStr = exchange.getRequest().getQueryParams().getFirst("stock");

            // 2. 检查参数是否存在
            if (stockStr == null || stockStr.isEmpty()) {
                log.warn("请求缺少 'stock' 查询参数，无法应用 Stock 断言");
                return false; // 如果缺少 stock 参数，则不匹配此路由
            }

            // 3. 尝试将参数解析为整数
            try {
                int stockValue = Integer.parseInt(stockStr);

                // 4. 检查库存值是否在配置的范围内 (包含边界)
                boolean match = stockValue >= config.getMinStock() && stockValue <= config.getMaxStock();
                if (match) {
                    log.debug("Stock 断言匹配成功: stock={}, 范围=[{}, {}]", stockValue, config.getMinStock(), config.getMaxStock());
                } else {
                    log.debug("Stock 断言匹配失败: stock={}, 范围=[{}, {}]", stockValue, config.getMinStock(), config.getMaxStock());
                }
                return match; // 返回匹配结果

            } catch (NumberFormatException e) {
                log.error("无法将 'stock' 参数 '{}' 解析为整数", stockStr, e);
                return false; // 如果参数格式不正确，则不匹配
            }
        };
    }

    /**
     * 配置类，用于接收 application.yml 中的参数
     */
    @Validated // 开启 JSR-303 验证
    public static class Config {

        @NotNull // 确保 minStock 不为 null
        private Integer minStock; // 最小库存值 (包含)

        @NotNull // 确保 maxStock 不为 null
        private Integer maxStock; // 最大库存值 (包含)

        // --- Getter 和 Setter ---
        public Integer getMinStock() {
            return minStock;
        }

        public void setMinStock(Integer minStock) {
            this.minStock = minStock;
        }

        public Integer getMaxStock() {
            return maxStock;
        }

        public void setMaxStock(Integer maxStock) {
            this.maxStock = maxStock;
        }
    }
} 