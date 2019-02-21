package com.zhou.workflowSystem.workflow.model;

import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;

public class CustomProcessInstance implements ProcessInstance {
    
    private String id;
    
    private boolean isEnded;
    
    private String activityId;
    
    private String processInstanceId;
    
    private String parentId;
    
    private String superExecutionId;
    
    private String processDefinitionId;
    
    private String processDefinitionName;
    
    private String processDefinitionKey;
    
    private Integer processDefinitionVersion;
    
    private String deploymentId;
    
    private String businessKey;
    
    private boolean isSuspended;
    
    private Map<String, Object> processVariables;
    
    private String tenantId;
    
    private String name;
    
    private String description;
    
    private String localizedName;
    
    private String localizedDescription;
    
    public CustomProcessInstance(ProcessInstance processInstance) {
        this.id = processInstance.getId();
        this.isEnded = processInstance.isEnded();
        
        this.activityId = processInstance.getActivityId();
        
        this.processInstanceId = processInstance.getProcessInstanceId();
        
        this.parentId = processInstance.getParentId();
        
        this.superExecutionId = processInstance.getSuperExecutionId();
        
        this.processDefinitionId = processInstance.getProcessDefinitionId();
        
        this.processDefinitionName = processInstance.getProcessDefinitionName();
        
        this.processDefinitionKey = processInstance.getProcessDefinitionKey();
        
        this.processDefinitionVersion = processInstance.getProcessDefinitionVersion();
        
        this.deploymentId = processInstance.getDeploymentId();
        
        this.businessKey = processInstance.getBusinessKey();
        
        this.isSuspended = processInstance.isSuspended();
        
        this.processVariables = processInstance.getProcessVariables();
        
        this.tenantId = processInstance.getTenantId();
        
        this.name = processInstance.getName();
        
        this.description = processInstance.getDescription();
        
        this.localizedName = processInstance.getLocalizedName();
        
        this.localizedDescription = processInstance.getLocalizedDescription();
        
    }
    
    public String getId() {
        return id;
    }
    
    public boolean isEnded() {
        return isEnded;
    }
    
    public String getActivityId() {
        return activityId;
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public String getSuperExecutionId() {
        return superExecutionId;
    }
    
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }
    
    public String getProcessDefinitionName() {
        return processDefinitionName;
    }
    
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }
    
    public Integer getProcessDefinitionVersion() {
        return processDefinitionVersion;
    }
    
    public String getDeploymentId() {
        return deploymentId;
    }
    
    public String getBusinessKey() {
        return businessKey;
    }
    
    public boolean isSuspended() {
        return isSuspended;
    }
    
    public Map<String, Object> getProcessVariables() {
        return processVariables;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getLocalizedName() {
        return localizedName;
    }
    
    public String getLocalizedDescription() {
        return localizedDescription;
    }
    
}
