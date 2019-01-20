package com.zhou.utils.common.model;

import lombok.Data;

/**
 * @Title:统一返回格式 model
 * @Description:
 * @Author:zhou
 * @Since:2019年1月16日
 * @Version:1.1.0
 */
@Data
public class Result {
    
    private Integer status; //返回结果状态码
    
    private String message; //返回结果说明
    
    private Object data; //返回结果数据
}
