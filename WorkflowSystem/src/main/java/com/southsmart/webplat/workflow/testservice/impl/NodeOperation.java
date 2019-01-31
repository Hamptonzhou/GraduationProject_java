package com.southsmart.webplat.workflow.testservice.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.southsmart.webplat.workflow.constant.WorkflowConstant;

@Service
public class NodeOperation {
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    ProcessEngine processEngine;
    
    @Autowired
    private HistoryService historyService;
    
    @Autowired
    private ManagementService managementService;
    
    @Autowired
    private TaskService taskService;
    
    /**
     * 工作流回退操作
     * 
     * @param currentTaskID 当前的任务id
     * @param destinationTaskID 回退的任务id
     * @Description:
     */
    public void rollbackTask(String currentTaskID, String destinationTaskID) {
        //根据要跳转的任务ID获取其任务
        HistoricTaskInstance hisTask =
            historyService.createHistoricTaskInstanceQuery().taskId(destinationTaskID).singleResult();
        //进而获取流程实例
        HistoricTaskInstance now_task =
            historyService.createHistoricTaskInstanceQuery().taskId(currentTaskID).singleResult();
        
        //取得当前待办任务的流程实例id
        String executionId = now_task.getExecutionId();
        //        String executionId = now_task.getProcessInstanceId();
        //取得流程定义
        ProcessDefinitionEntity definition =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(hisTask.getProcessDefinitionId());
        //获取历史任务的Activity
        ActivityImpl hisActivity = definition.findActivity(hisTask.getTaskDefinitionKey());
        //实现跳转
        managementService.executeCommand(new JumpCmd(executionId, hisActivity.getId()));
    }
    
    /**
     * 工作流回退操作 只能实现回退，不能实现向后跳转
     * 
     * @param currentTaskID 当前的任务id
     * @param destinationTaskID 回退的任务id
     * @Description:
     */
    public void rollbackTask2(String currentTaskID, String destinationTaskID) {
        currentTaskID = "50103";
        destinationTaskID = "50083";
        
        //根据要跳转的任务ID获取其任务
        HistoricTaskInstance hisTask =
            historyService.createHistoricTaskInstanceQuery().taskId(destinationTaskID).singleResult();
        //进而获取流程实例
        Task current_task = taskService.createTaskQuery().taskId(currentTaskID).singleResult();
        //取得流程定义
        ProcessDefinitionEntity definition =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(hisTask.getProcessDefinitionId());
        //获取历史任务的Activity
        ActivityImpl currActivity = definition.findActivity(current_task.getTaskDefinitionKey());
        
        // 取得返回任务的活动
        ActivityImpl historyTaskActivity = definition.findActivity(hisTask.getTaskDefinitionKey());
        
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
        
        // 获取当前接的的后期跳转路线
        List<PvmTransition> pvmTransitionList = currActivity.getOutgoingTransitions();
        oriPvmTransitionList.addAll(pvmTransitionList);
        
        pvmTransitionList.clear();
        
        // 建立新方向
        TransitionImpl newTransition = currActivity.createOutgoingTransition();
        newTransition.setDestination(historyTaskActivity);
        
        try {
            //完成任务
            //time2 = System.currentTimeMillis();
            taskService.setVariableLocal(current_task.getId(),
                WorkflowConstant.ACT_JUMP_TYPE,
                WorkflowConstant.ACT_JUMP_TYPE_SEND_BACK);
            this.taskService.complete(current_task.getId());
            //time3 = System.currentTimeMillis();
        } finally {
            // 恢复方向
            // 移除动态添加跳转的线
            currActivity.getIncomingTransitions().remove(newTransition);
            List<PvmTransition> pvmTList = currActivity.getOutgoingTransitions();
            pvmTList.clear();
            pvmTList.addAll(oriPvmTransitionList);
        }
        
    }
    
    /**
     * 召回任务
     * 
     * @param taskId
     * @Description:
     */
    public void recordTask(String taskId) {
        //根据要跳转的任务ID获取其任务
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        //进而获取流程实例
        String executionId = hisTask.getExecutionId();
        /* ProcessInstance instance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(hisTask.getProcessInstanceId())
            .singleResult();*/
        //         taskService.createTaskQuery().processInstanceId(hisTask.getProcessInstanceId()).ac
        //取得流程定义
        ProcessDefinitionEntity definition =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(hisTask.getProcessDefinitionId());
        //获取历史任务的Activity
        ActivityImpl hisActivity = definition.findActivity(hisTask.getTaskDefinitionKey());
        //实现跳转
        managementService.executeCommand(new JumpCmd(executionId, hisActivity.getId()));
    }
    
    /**
     * 自由挑转
     * 
     * @param currentTaskID
     * @param destinationTaskList
     * @Description:
     */
    public void jumpTask(String currentTaskID, List<String> destinationTaskList) {
        currentTaskID = "50107";
        destinationTaskList = Arrays.asList("50103", "50099");
        //进而获取流程实例
        Task current_task = taskService.createTaskQuery().taskId(currentTaskID).singleResult();
        //取得流程定义
        ProcessDefinitionEntity definition =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(current_task.getProcessDefinitionId());
        //获取当前任务的Activity
        ActivityImpl currActivity = definition.findActivity(current_task.getTaskDefinitionKey());
        
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
        
        // 获取当前接的的后期跳转路线
        List<PvmTransition> pvmTransitionList = currActivity.getOutgoingTransitions();
        oriPvmTransitionList.addAll(pvmTransitionList);
        
        pvmTransitionList.clear();
        
        List<PvmTransition> newTransitionList = new ArrayList<>();
        //获取到跳转的目的任务
        for (String destinationTask : destinationTaskList) {
            //根据要跳转的任务ID获取其任务
            HistoricTaskInstance hisTask =
                historyService.createHistoricTaskInstanceQuery().taskId(destinationTask).singleResult();
            // 取得返回任务的活动
            ActivityImpl historyTaskActivity = definition.findActivity(hisTask.getTaskDefinitionKey());
            // 建立新方向
            TransitionImpl newTransition = currActivity.createOutgoingTransition();
            newTransition.setDestination(historyTaskActivity);
            newTransitionList.add(newTransition);
        }
        
        try {
            //完成任务
            taskService.complete(current_task.getId());
        } finally {
            // 恢复方向
            // 移除动态添加跳转的线
            //            currActivity.getIncomingTransitions().remove(newTransition);
            currActivity.getIncomingTransitions().removeAll(newTransitionList);
            List<PvmTransition> pvmTList = currActivity.getOutgoingTransitions();
            pvmTList.clear();
            pvmTList.addAll(oriPvmTransitionList);
        }
    }
    
    public void TaskOperation() {
        String taskId = "60024";
        //        taskService.resolveTask(taskId);
        //        taskService.deleteTask(taskId);
        //        taskService.setAssignee(taskId, "kermit");
        /*List<IdentityLink> links = taskService.getIdentityLinksForTask(taskId);
        if (links != null && links.size() > 0) {
            for(IdentityLink identityLink : links) {
               if(IdentityLinkType.ASSIGNEE.equals(identityLink.getType())) {
                   System.out.println("任务已经被签收");
                   
               }else if(IdentityLinkType.CANDIDATE.equals(identityLink.getType())){
                   System.out.println("");
               } 
            }
        }*/
        
        //        System.out.println(links);
        //        taskService.addCandidateUser(taskId, "kermit");
        taskService.claim(taskId, "kermit");
    }
    
    public void printFlow() {
        String taskId = "40018";
        //        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        String curNodeId = hisTask.getTaskDefinitionKey();
        String actDefId = hisTask.getProcessDefinitionId();
        //修改流程定义  
        ProcessDefinitionEntity processDefinition =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(actDefId);
        
        ActivityImpl curAct = processDefinition.findActivity(curNodeId);
        List<PvmTransition> outTrans = curAct.getOutgoingTransitions();
        
        printByCricle(outTrans);
    }
    
    private void printByCricle(List<PvmTransition> outTrans) {
        for (PvmTransition tr : outTrans) {
            PvmActivity ac = tr.getDestination();
            String type = ac.getProperty("type").toString();
            System.out.println("--->>> id: " + ac.getId() + "，类型：" + type + "，总体信息：" + ac);
            List<PvmTransition> ch_outTrans = ac.getOutgoingTransitions();
            printByCricle(ch_outTrans);
        }
    }
}
