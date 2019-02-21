package com.zhou.workflowSystem.workflow.service;

import com.zhou.workflowSystem.workflow.entity.ProcessDefinitionTree;

/**
 * 自定义查询接口声明
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月6日
 * @Version:1.1.0
 */
public interface ICustomService {
    
    /**
     * 获取流程定义树
     * 
     * @return
     * @Description:
     */
    ProcessDefinitionTree getProcessDefinitionTree();
    
}
