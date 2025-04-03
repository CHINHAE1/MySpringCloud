package com.woniuxy.registrar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Map;

/**
 * 这个类是：
 *
 * @author: CHINHAE
 * @date: 2025/4/3/周四 13:02
 * @version: 1.0
 */
@Component
@EnableScheduling
public class ServiceRegistration implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${server.port}")
    private String port;

    // 允许在配置中指定IP,否则自动获取
    @Value("${service.ip:#{null}}")
    private String configuredIp;

    // 服务IP地址
    private String serviceIp;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // 在应用启动时自动注册到Order服务
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 确定IP地址
        serviceIp = (configuredIp != null) ? configuredIp : getLocalIp();
        System.out.println("当前服务IP: " + serviceIp + ", 端口: " + port);
        
        // 自动注册到Order服务
        registerToOrderService();
    }

    // 定期注册保持心跳
    @Scheduled(fixedRate = 30000)  // 每30秒执行一次
    public void registerToOrderService() {
        try {
            String orderServiceUrl = "http://localhost:8091"; // Order服务地址
            String registrationUrl = orderServiceUrl + "/service/register?host=" + serviceIp + "&port=" + port;
            restTemplate.postForEntity(registrationUrl, null, Map.class);
            System.out.println("成功注册到Order服务: " + serviceIp + ":" + port);
        } catch (Exception e) {
            System.err.println("注册服务失败: " + e.getMessage());
        }
    }

    // 获取本机IP地址
    private String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
            // 如果无法获取合适的IP，使用本地IP
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }
}
