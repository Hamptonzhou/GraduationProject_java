package com.zhou.packageSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 解决前端发送ajax因为跨域而无法访问的问题，统一打包后，不再单独在项目配置
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月3日
 * @Version:1.1.0
 */
@Configuration
public class CORSConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//设置允许跨域的路径
            .allowedOrigins("*")//设置允许跨域请求的域名
            .allowCredentials(true)//是否允许证书 不再默认开启
            .allowedMethods("GET", "POST", "PUT", "DELETE")//设置允许的方法
            .maxAge(3600);//跨域允许时间
    }
}
