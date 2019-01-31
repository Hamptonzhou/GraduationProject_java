package com.southsmart.webplat.workflow.model;

import java.util.Date;
import java.util.Map;

import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;

public class CustomActivitiTask implements Task {
    
    private String id;
    
    private String name;
    
    private String description;
    
    private int priority;
    
    private String owner;
    
    private String assignee;
    
    private String processInstanceId;
    
    private String executionId;
    
    private String processDefinitionId;
    
    private Date createTime;
    
    private String taskDefinitionKey;
    
    private Date dueDate;
    
    private String category;
    
    private String parentTaskId;
    
    private String tenantId;
    
    private String formKey;
    
    private Map<String, Object> taskLocalVariables;
    
    private Map<String, Object> processVariables;
    
    private DelegationState delegationState;
    
    private boolean isSuspended;
    
    public CustomActivitiTask(Task task) {
        this.id = task.getId();
        setName(task.getName());
        setDescription(task.getDescription());
        setPriority(task.getPriority());
        setOwner(task.getOwner());
        setAssignee(task.getAssignee());
        setProcessInstanceId(task.getProcessInstanceId());
        setExecutionId(task.getExecutionId());
        setProcessDefinitionId(task.getProcessDefinitionId());
        setCreateTime(task.getCreateTime());
        setTaskDefinitionKey(task.getTaskDefinitionKey());
        setDueDate(task.getDueDate());
        setCategory(task.getCategory());
        setParentTaskId(task.getParentTaskId());
        setTenantId(task.getTenantId());
        setFormKey(task.getFormKey());
        setTaskLocalVariables(task.getTaskLocalVariables());
        setProcessVariables(task.getProcessVariables());
        setDelegationState(task.getDelegationState());
        this.isSuspended = task.isSuspended();
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public String getExecutionId() {
        return executionId;
    }
    
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
    
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }
    
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }
    
    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }
    
    public Map<String, Object> getTaskLocalVariables() {
        return taskLocalVariables;
    }
    
    public void setTaskLocalVariables(Map<String, Object> taskLocalVariables) {
        this.taskLocalVariables = taskLocalVariables;
    }
    
    public Map<String, Object> getProcessVariables() {
        return processVariables;
    }
    
    public void setProcessVariables(Map<String, Object> processVariables) {
        this.processVariables = processVariables;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getAssignee() {
        return assignee;
    }
    
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public void delegate(String userId) {
    }
    
    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
    
    public String getParentTaskId() {
        return parentTaskId;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    @Override
    public String getFormKey() {
        return formKey;
    }
    
    @Override
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }
    
    @Override
    public void setLocalizedName(String name) {
        setName(name);
        
    }
    
    @Override
    public void setLocalizedDescription(String description) {
        setDescription(description);
    }
    
    @Override
    public boolean isSuspended() {
        return this.isSuspended;
    }
    
    public DelegationState getDelegationState() {
        return delegationState;
    }
    
    public void setDelegationState(DelegationState delegationState) {
        this.delegationState = delegationState;
    }
    
    public void setSuspended(boolean isSuspended) {
        this.isSuspended = isSuspended;
    }
    
}
