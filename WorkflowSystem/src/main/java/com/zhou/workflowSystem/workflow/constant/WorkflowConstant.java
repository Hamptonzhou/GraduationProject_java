package com.zhou.workflowSystem.workflow.constant;

public interface WorkflowConstant {
    /**
     * 跳转类型
     */
    static final String ACT_JUMP_TYPE = "jumpType";
    
    /**
     * 回退或撤回
     */
    static final String ACT_JUMP_TYPE_SEND_BACK = "sendBack";
    
    /**
     * 正常流转
     */
    static final String ACT_JUMP_TYPE_NORMAL_FLOW = "normalFlow";
    
    /**
     * 上一节点的id
     */
    static final String ACT_LAST_TASK_ID = "lastTaskId";
    
    /**
     * 活动的类型-开始
     */
    static final String ACT_ACTIVITY_TYPE_START_EVENT = "startEvent";
    
    /**
     * 活动的类型-结束
     */
    static final String ACT_ACTIVITY_TYPE_END_EVENT = "endEvent";
    
    /**
     * 活动的类型-网关
     */
    static final String ACT_ACTIVITY_TYPE_GATEWAY = "Gateway";
    
    /**
     * 活动的类型-并行网关
     */
    static final String ACT_ACTIVITY_TYPE_PARALLEL_GATEWAY = "parallelGateway";
    
    /**
     * 活动的类型-用户任务
     */
    static final String ACT_ACTIVITY_TYPE_USER_TASK = "userTask";
    
    /**
     * PvmActivity类property中的类型
     */
    static final String ACT_PVMACTIVITY_PROPERTY_TYPE = "type";
}
