package com.zhou.workflowSystem.workflow.entity;

import java.util.Map;

import com.zhou.workflowSystem.workflow.model.CustomActivitiTask;
import com.zhou.workflowSystem.workflow.model.CustomProcessDefinition;
import com.zhou.workflowSystem.workflow.model.CustomProcessInstance;

import lombok.Data;

/**
 * 封装我的工作模块查询的列表
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月23日
 * @Version:1.1.0
 */
@Data
public class MyWorkEntity {
    
    /**
     * 通用属性
     */
    private String userId;
    
    //业务名称，使用流程定义名称即可
    private String businessName;
    
    //流程变量
    private Map<String, Object> variables;
    
    // 流程任务
    private CustomActivitiTask task;
    
    // 运行中的流程实例
    private CustomProcessInstance processInstance;
    
    /**
     * 在办工作表格属性
     */
    //    private String businessAcceptNumber;
    //    
    //    private String businessName;
    //    
    //    private String businessStartTime;
    //    
    //    private String taskName;
    //    
    //    private String taskStartTime;
    //    
    //    private String claimTime;
    //    
    //    private String processDefinitionImage;
    //    
    //    private String businessForm;
}
