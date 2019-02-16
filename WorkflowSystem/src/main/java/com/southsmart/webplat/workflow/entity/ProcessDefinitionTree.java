package com.southsmart.webplat.workflow.entity;

import java.util.List;

import org.activiti.engine.repository.ProcessDefinition;

import lombok.Data;

/**
 * 流程定义树实体
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月15日
 * @Version:1.1.0
 */
@Data
public class ProcessDefinitionTree {
    private String id;
    
    private String title;
    
    private List<ProcessDefinitionTree> children;
    
    public ProcessDefinitionTree() {
        super();
    }
    
    public ProcessDefinitionTree(String id, String title, List<ProcessDefinitionTree> children) {
        super();
        this.id = id;
        this.title = title;
        this.children = children;
    }
    
}
