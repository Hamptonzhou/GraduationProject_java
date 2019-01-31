package com.southsmart.webplat.workflow.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.Execution;

import lombok.Data;

@Entity
@Table(name = "ACT_RU_PATH")
@Data
public class OperationRecord {
    //主键id
    @Id
    @Column(name = "ID_", length = 64)
    private String id;
    
    //流程定义的id
    @Column(name = "PROC_DEF_ID_", length = 64)
    private String processDefinitionId;
    
    //流程实例id
    @Column(name = "PROC_INST_ID_", length = 64)
    private String processInstanceId;
    
    //节点定义的id
    @Column(name = "TASK_DEF_KEY_", length = 255)
    private String taskDefinitionKey;
    
    //执行的id
    @Column(name = "EXECUTION_ID_", length = 64)
    private String executuinId;
    
    //执行的父id
    @Column(name = "EXECUTION_PARENT_ID_", length = 64)
    private String executuinParentId;
    
    //任务id
    @Column(name = "TASK_ID_", length = 64)
    private String taskId;
    
    //父任务id
    @Column(name = "PARENT_TASK_ID_", length = 64)
    private String parentTaskId;
    
    //描述
    @Column(name = "DESCRIPTION_", length = 3000)
    private String description;
    
    //处理人
    @Column(name = "ASSIGNEE_", length = 255)
    private String userId;
    
    //跳转方式  正常跳转、回退跳转
    @Column(name = "JUMP_TYPE_", length = 64)
    private String jumpType;
    
    //处理时间
    @Column(name = "DUE_DATE_", length = 64)
    @Temporal(TemporalType.TIMESTAMP)
    private Date duelDate;
    
    //上一步任务id
    @Column(name = "LAST_TASK_ID_", length = 64)
    private String lastTaskId;
    
    public OperationRecord(TaskEntity task, Execution execution) {
        this.id = task.getId();
        this.taskId = task.getId();
        this.processDefinitionId = task.getProcessDefinitionId();
        this.processInstanceId = task.getProcessInstanceId();
        this.taskDefinitionKey = task.getTaskDefinitionKey();
        this.parentTaskId = task.getParentTaskId();
        this.executuinId = execution.getId();
        this.executuinParentId = execution.getParentId();
    }
    
    public OperationRecord() {
        
    }
}
