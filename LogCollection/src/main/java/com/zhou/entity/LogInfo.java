package com.zhou.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * 日志消息实体类，存储与MongoDB
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月15日
 * @Version:1.1.0
 */
@Document(collection = "logCollection")
@Data
public class LogInfo {
    /**
     * mongoDB中的主键，自动生成
     */
    @Id
    private String id;
    
    /**
     * 操作时间
     */
    private String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    
    /**
     * 操作方法名，一般与真实方法名相同，以方便定位。
     */
    private String method;
    
    /**
     * 调用方法的参数信息，一般格式为：“(参数类型):参数值”
     */
    private String parameter;
    
    /**
     * 当前操作用户id
     */
    private String userId;
    
    /**
     * 当前用户访问的客户端信息
     */
    private String clientIp;
    
    /**
     * 请求信息
     */
    private String requestUri;
    
    /**
     * 执行时间
     */
    private Long executionTime;
    
    /**
     * 操作的简要描述信息，方便理解。
     */
    private String description;
    
    public LogInfo() {
        super();
    }
    
    public LogInfo(String method, String parameter, String userId, String clientIp, String requestUri,
        Long executionTime, String description) {
        super();
        this.method = method;
        this.parameter = parameter;
        this.userId = userId;
        this.clientIp = clientIp;
        this.requestUri = requestUri;
        this.executionTime = executionTime;
        this.description = description;
    }
}
