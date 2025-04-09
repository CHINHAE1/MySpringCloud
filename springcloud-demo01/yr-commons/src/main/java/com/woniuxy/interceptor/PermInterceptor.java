package com.woniuxy.interceptor;

import com.woniuxy.anno.PermAccess;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Author: 马宇航
 * @Todo: 权限拦截器
 * @DateTime: 25/04/09/星期三 17:36
 * @Component: 成都蜗牛学苑
 **/
public class PermInterceptor extends HandlerInterceptorAdapter {
    /**
     * 进入controller之前有效果
     * ChangeLog : 1. 创建 (25/04/09/星期三 17:37 [马宇航]);
     * @param request
     * @param response
     * @param handler controller对象，目标进入的controller对象，
     * @return boolean true就会放行
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(handler);
        //直接获取方法上的注解
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            //判断方法上是否有PermAccess注解
            if (method.isAnnotationPresent(PermAccess.class)) {
                //如果存在，则进行值的获取，来校验，用户权限和注解配置的权限是否匹配
                PermAccess permAccess = method.getAnnotation(PermAccess.class);
                String value = permAccess.value();
                //"order::add" 是写死的，但是实际上，是根据refreshToken去redis中获取 用户的信息，拿到用户的权限列表
                if(value.equals("order::add")){
                    System.out.println("权限校验通过");
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * controller执行之后有效果
     * ChangeLog : 1. 创建 (25/04/09/星期三 17:37 [马宇航]);
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @return void
    */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
} 