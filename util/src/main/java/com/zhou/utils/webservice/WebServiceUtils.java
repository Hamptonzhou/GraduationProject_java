package com.zhou.utils.webservice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

/**
 * 用于WebService的常用方法.
 *
 * @author hgc
 */
@Slf4j
public final class WebServiceUtils {
    
    /**
     * 存放在seesion的userId名称
     */
    private final static String USERID_IN_SESSION_NAME = "USERID_IN_SESSION";
    
    /**
     * 获取当前请求对象，需要在web.xml中配置RequestContextListener监听器
     * （这样可使得Request对象与Controller中的更一致，同时也可防止线程方面的问题）：<br/>
     * &lt;listener&gt;<br/>
     * &lt;listener-class&gt;<br/>
     * org.springframeworntext.request.RequestContextListener<br/>
     * &lt;/listener-class&gt;<br/>
     * &lt;/listener&gt;<br/>
     * 注：RequestContextListener监听器主要用来解决Request作用域的Bean管理。
     * 
     * @return
     */
    public static HttpServletRequest getRequest() {
        HttpServletRequest request = null;
        ServletRequestAttributes servletRequestAttributes =
            (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null)
            request = servletRequestAttributes.getRequest();
        return request;
    }
    
    /**
     * 获取当前回应对象，需要在web.xml中配置RequestContextListener监听器（参见getRequest）
     * 
     * @return
     */
    public static HttpServletResponse getResponse() {
        HttpServletResponse response = null;
        ServletRequestAttributes servletRequestAttributes =
            (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null)
            response = servletRequestAttributes.getResponse();
        return response;
    }
    
    /**
     * 获取当前用户ID，需要在web.xml中配置RequestContextListener监听器（参见getRequest）。
     * 先从Cas的AttributePrincipal查找，未找到再从HttpSession的Attribute中查找
     * 
     * @return 未找到，返回null
     */
    public static String getCurrentUserId() {
        return getCurrentUserId(getRequest());
    }
    
    /**
     * 需要重新实现
     * 
     * @param request 需要传入的请求对象
     * @return 未找到，返回null
     */
    public static String getCurrentUserId(HttpServletRequest request) {
        HttpSession session = null;
        try {
            session = request.getSession(false);
        } catch (Exception e) {
            log.info("getCurrentUserId出错！");
            e.printStackTrace();
        }
        if (session == null)
            return null;
        String userId = (String)session.getAttribute(USERID_IN_SESSION_NAME);
        return userId;
    }
}
