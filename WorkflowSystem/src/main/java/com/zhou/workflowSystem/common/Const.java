package com.zhou.workflowSystem.common;

/**
 * 常量
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月26日
 * @Version:1.1.0
 */
public class Const {
    /**
     * 组任务的类型常量
     * 
     * @Title:
     * @Description: 定义task是否被接办的流程变量的常量
     * @Author:zhou
     * @Since:2019年2月26日
     * @Version:1.1.0
     */
    public interface ClaimStatus {
        public static final String KEY = "claimOrNot";
        
        public static final String CLAIMED = "已接办";
        
        public static final String UNCLAIM = "未接办";
    }
    
    /**
     * 备注内容
     * 
     * @Title:
     * @Description: 定义流程变量的常量
     * @Author:zhou
     * @Since:2019年2月26日
     * @Version:1.1.0
     */
    public interface RemarkContent {
        public static final String KEY = "remarkContent";
    }
}
