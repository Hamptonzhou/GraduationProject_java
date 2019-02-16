package com.southsmart.webplat.workflow.service;

import com.southsmart.webplat.workflow.entity.ProcessDefinitionTree;

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
