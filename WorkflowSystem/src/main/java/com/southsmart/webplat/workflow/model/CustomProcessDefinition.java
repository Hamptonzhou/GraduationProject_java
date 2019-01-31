package com.southsmart.webplat.workflow.model;

import org.activiti.engine.repository.ProcessDefinition;

public class CustomProcessDefinition implements ProcessDefinition {
    
    private String id;
    
    private String category;
    
    private String name;
    
    private String key;
    
    private String description;
    
    private int version;
    
    private String resourceName;
    
    private String deploymentId;
    
    private String diagramResourceName;
    
    private boolean hasStartFormKey;
    
    private boolean hasGraphicalNotation;
    
    private boolean isSuspended;
    
    private String tenantId;
    
    public CustomProcessDefinition(ProcessDefinition processDefinition) {
        this.id = processDefinition.getId();
        this.category = processDefinition.getCategory();
        this.name = processDefinition.getName();
        this.key = processDefinition.getKey();
        this.description = processDefinition.getDescription();
        this.version = processDefinition.getVersion();
        this.resourceName = processDefinition.getResourceName();
        this.deploymentId = processDefinition.getDeploymentId();
        this.diagramResourceName = processDefinition.getDiagramResourceName();
        this.hasStartFormKey = processDefinition.hasStartFormKey();
        this.hasGraphicalNotation = processDefinition.hasGraphicalNotation();
        this.isSuspended = processDefinition.isSuspended();
        this.tenantId = processDefinition.getTenantId();
        
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public String getCategory() {
        return this.category;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getKey() {
        return this.key;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public int getVersion() {
        return this.version;
    }
    
    @Override
    public String getResourceName() {
        return this.resourceName;
    }
    
    @Override
    public String getDeploymentId() {
        return this.deploymentId;
    }
    
    @Override
    public String getDiagramResourceName() {
        return this.diagramResourceName;
    }
    
    @Override
    public boolean hasStartFormKey() {
        return this.hasStartFormKey;
    }
    
    @Override
    public boolean hasGraphicalNotation() {
        return this.hasGraphicalNotation;
    }
    
    @Override
    public boolean isSuspended() {
        return this.isSuspended;
    }
    
    @Override
    public String getTenantId() {
        return this.tenantId;
    }
    
}
