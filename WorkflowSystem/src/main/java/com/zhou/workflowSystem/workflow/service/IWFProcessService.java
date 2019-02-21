package com.zhou.workflowSystem.workflow.service;

public interface IWFProcessService {
    static final String SERVER_BEAN_NAME = "wfProcessService";
    
    boolean checkProcessIsEnd(String taskId);
}
