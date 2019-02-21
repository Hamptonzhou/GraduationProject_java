package com.zhou.workflowSystem.common.model;

/**
 * 统一返回格式 model
 */
public class Result {
    
    private Integer status; //返回结果状态码
    
    private String message; //返回结果说明
    
    private Object data; //返回结果数据
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
}
