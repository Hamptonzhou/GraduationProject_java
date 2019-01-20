package com.zhou.utils.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: CharacterEncodingFilter
 * @Description: 此过滤器用来解决全站中文乱码问题
 * @author: hgc
 */
public class CharacterEncodingFilter implements Filter {
    
    private FilterConfig filterConfig = null;
    
    // 设置默认的字符编码
    private String defaultCharset = "UTF-8";
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
        throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        String charset = filterConfig.getInitParameter("charset");
        if (charset == null) {
            charset = defaultCharset;
        }
        request.setCharacterEncoding(charset);
        response.setCharacterEncoding(charset);
        response.setContentType("text/html;charset=" + charset);
        
        MyCharacterEncodingRequest requestWrapper = new MyCharacterEncodingRequest(request);
        chain.doFilter(requestWrapper, response);
    }
    
    @Override
    public void init(FilterConfig filterConfig)
        throws ServletException {
        // 得到过滤器的初始化配置信息
        this.filterConfig = filterConfig;
    }
    
    @Override
    public void destroy() {
        
    }
}

/*
 * 1.实现与被增强对象相同的接口 2、定义一个变量记住被增强对象 3、定义一个构造器，接收被增强对象 4、覆盖需要增强的方法
 * 5、对于不想增强的方法，直接调用被增强对象（目标对象）的方法
 */

class MyCharacterEncodingRequest extends HttpServletRequestWrapper {
    
    private HttpServletRequest request;
    
    public MyCharacterEncodingRequest(HttpServletRequest request) {
        super(request);
        this.request = request;
    }
    
    /*
     * 重写getParameter方法
     * 
     * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name) {
        
        try {
            // 获取参数的值
            String value = this.request.getParameter(name);
            if (value == null) {
                return null;
            }
            // 如果不是以get方式提交数据的，就直接返回获取到的值
            if (!this.request.getMethod().equalsIgnoreCase("get")) {
                return value;
            } else {
                // 如果是以get方式提交数据的，就对获取到的值进行转码处理
                value = new String(value.getBytes("ISO8859-1"), this.request.getCharacterEncoding());
                return value;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
