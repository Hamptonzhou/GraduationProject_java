package com.southsmart.webplat.workflow.controller;

import java.io.IOException;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.southsmart.webplat.common.model.Result;
import com.southsmart.webplat.common.util.ResultUtil;
import com.southsmart.webplat.workflow.exception.WorkflowException;
import com.southsmart.webplat.workflow.model.CustomProcessDefinition;
import com.southsmart.webplat.workflow.testservice.impl.NodeOperation;
import com.southsmart.webplat.workflow.testservice.impl.baseWorkFlowServiceImpl;

@RestController
@RequestMapping("base")
public class BaseWorkflowController {
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private baseWorkFlowServiceImpl baseWorkFlowService;
    
    @Autowired
    private NodeOperation nodeOperation;
    
    @RequestMapping("repository")
    public Result repository() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("CreateProcessInWeb")
            .latestVersion()
            .singleResult();
        return ResultUtil.success(new CustomProcessDefinition(processDefinition));
    }
    
    @RequestMapping("baseWorkFlowService")
    /**/
    public Result baseProcessService()
        throws WorkflowException, IOException {
        //        String filePath = "D:\\Downloads\\";
        //        baseWorkFlowService.getDueTaskHignlightImage("2501", filePath);//当前环节高亮
        //        baseWorkFlowService.traceProcessImage("2501", filePath);//带高亮流程
        String taskId = "11";
        ActivityImpl activiti = baseWorkFlowService.findActivity(taskId);//带高亮流程
        TaskDefinition taskEntity = baseWorkFlowService.findTaskDefinitionByActivityImpl(activiti);
        return ResultUtil.success(taskEntity);
    }
    
    /**
     * 复制到自己的例子中实现以下，查看数据库表的历史数据会不会被删除，有什么变化
     */
    @RequestMapping("NodeOperation")
    public Result NodeOperation()
        throws WorkflowException, IOException {
        //        String taskId = "5003";
        String currentTaskID = "52515";
        String destinationTaskID = "52524";
        nodeOperation.rollbackTask(currentTaskID, destinationTaskID);
        return ResultUtil.success();
    }
    
    @RequestMapping("showAllOfProcess")
    /**/
    public void showAllOfProcess() {
        String processInstanceId = "17505";
        baseWorkFlowService.showAllOfProcess(processInstanceId);
    }
    
    @RequestMapping("jumpTask")
    /**/
    public void jumpTask() {
        nodeOperation.jumpTask(null, null);
    }
    
    @RequestMapping("TaskOperation")
    /**/
    public void TaskOperation() {
        nodeOperation.TaskOperation();
    }
    
}
