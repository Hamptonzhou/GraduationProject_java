package com.zhou.workflowSystem.workflow.testservice.impl;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;

public class JumpCmd implements Command<ExecutionEntity> {
    
    private String executionId;
    
    private String activityId;
    
    public static final String REASION_DELETE = "deleted";
    
    /**
     * executionId 区分流程的树状结构，如果是主干，executionId = processInstanceId
     * 
     * @param executionId 当前任务的 executionId
     * @param activityId 跳转的节点
     */
    public JumpCmd(String executionId, String activityId) {
        this.executionId = executionId;
        this.activityId = activityId;
    }
    
    @Override
    public ExecutionEntity execute(CommandContext commandContext) {
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(executionId);
        executionEntity.destroyScope(REASION_DELETE);
        ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
        ActivityImpl activity = processDefinition.findActivity(activityId);
        executionEntity.executeActivity(activity);
        
        return executionEntity;
    }
    
}
