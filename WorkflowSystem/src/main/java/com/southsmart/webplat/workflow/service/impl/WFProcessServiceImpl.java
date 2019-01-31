package com.southsmart.webplat.workflow.service.impl;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import com.southsmart.webplat.workflow.service.IWFProcessService;

@Service(IWFProcessService.SERVER_BEAN_NAME)
public class WFProcessServiceImpl implements IWFProcessService {
    @Resource(type = RepositoryService.class)
    private RepositoryService repositoryService;
    
    @Resource(type = RuntimeService.class)
    private RuntimeService runtimeService;
    
    @Resource(type = ProcessEngine.class)
    private ProcessEngine processEngine;
    
    @Resource(type = HistoryService.class)
    private HistoryService historyService;
    
    @Resource(type = TaskService.class)
    private TaskService taskService;
    
    @Override
    public boolean checkProcessIsEnd(String taskId) {
        HistoricTaskInstance histask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(histask.getProcessInstanceId())
            .singleResult();
        if (processInstance == null) {
            return true;
        }
        return false;
    }
}
