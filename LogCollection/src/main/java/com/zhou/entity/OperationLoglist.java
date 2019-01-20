package com.zhou.entity;

import java.util.HashMap;

/**
 * 操作日志列表实体，继承hashmap
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月15日
 * @Version:1.1.0
 */
public class OperationLoglist extends HashMap<String, Object> {
    
    private static final long serialVersionUID = 3786627882881741176L;
    
    /**
     * 对应mongodb集合名
     */
    public final static String TABLE_NAME = "logCollection";
    
}
