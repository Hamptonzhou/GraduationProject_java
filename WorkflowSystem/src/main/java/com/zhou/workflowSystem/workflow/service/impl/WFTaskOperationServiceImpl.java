package com.zhou.workflowSystem.workflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.zhou.workflowSystem.workflow.entity.OperationRecord;
import com.zhou.workflowSystem.workflow.exception.WorkflowException;
import com.zhou.workflowSystem.workflow.service.IWFOperationRecordService;
import com.zhou.workflowSystem.workflow.service.IWFProcessService;
import com.zhou.workflowSystem.workflow.service.IWFTaskOperationService;
import com.zhou.workflowSystem.workflow.util.ProcessDefinitionUtil;

@Service(IWFTaskOperationService.SERVER_BEAN_NAME)
public class WFTaskOperationServiceImpl implements IWFTaskOperationService {
    
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
    
    @Resource(name = IWFOperationRecordService.SERVER_BEAN_NAME)
    private IWFOperationRecordService wfOperationRecordService;
    
    @Resource(name = IWFProcessService.SERVER_BEAN_NAME)
    private IWFProcessService wfProcessService;
    
    /**
     * 添加任务意见.
     *
     * @param taskId 任务Id
     * @param processInstanceId 流程实例Id
     * @param comment 意见
     * @throws WorkflowException
     */
    public void addTaskComment(String taskId, String processInstanceId, String comment)
        throws WorkflowException {
        taskService.addComment(taskId, processInstanceId, comment);
    }
    
    /**
     * 签收任务.
     *
     * @param taskId 任务Id
     * @param userId 办理人
     * @throws WorkflowException
     */
    public void claimTask(String taskId, String userId)
        throws WorkflowException {
        taskService.claim(taskId, userId);
    }
    
    /**
     * 退签任务.
     *
     * @param taskId 任务Id
     * @param userId 办理人
     * @throws WorkflowException
     */
    public boolean unclaimTask(String taskId, String userId)
        throws WorkflowException {
        //判定是否有候选人，如果有才能退签
        if (checkIdentityLink(taskId, IdentityLinkType.CANDIDATE)) {
            taskService.unclaim(taskId);
            return true;
        }
        return false;
    }
    
    /**
     * 判断任务人员的参与状态
     * 
     * @param taskId
     * @return
     * @Description:
     */
    private boolean checkIdentityLink(String taskId, String identityLinkType) {
        List<IdentityLink> links = taskService.getIdentityLinksForTask(taskId);
        if (links != null && links.size() > 0) {
            //判定是否已经签收
            if (IdentityLinkType.ASSIGNEE.equals(identityLinkType)) {
                for (IdentityLink identityLink : links) {
                    if (IdentityLinkType.ASSIGNEE.equals(identityLink.getType())) {
                        return true;
                    }
                }
            }
            //判定是否存在候选人
            if (IdentityLinkType.CANDIDATE.equals(identityLinkType)) {
                for (IdentityLink identityLink : links) {
                    if (IdentityLinkType.CANDIDATE.equals(identityLink.getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * 指派任务.
     *
     * @param taskId 任务Id
     * @param userId 办理人
     * @throws WorkflowException
     */
    public void assigneeTask(String taskId, String userId)
        throws WorkflowException {
        taskService.setAssignee(taskId, userId);
    }
    
    /**
     * 添加多个候选人
     * 
     * @param taskId
     * @param userIds
     * @throws WorkflowException
     * @Description:
     */
    public void assignCandidates(String taskId, List<String> userIds)
        throws WorkflowException {
        for (String userId : userIds)
            taskService.addCandidateUser(taskId, userId);
    }
    
    /**
     * 完成任务.
     *
     * @param taskId 任务Id
     * @param userId 办理人
     * @throws WorkflowException
     */
    public void completeTask(String taskId, Map<String, Object> variables)
        throws WorkflowException {
        taskService.complete(taskId, variables);
        
    }
    
    /**
     * 保存操作记录，这里需要完善
     * 
     * @param taskId
     * @param jumpType
     * @Description:
     */
    public void saveOperationRecord(String taskId, String jumpType) {
        TaskEntity task = (TaskEntity)taskService.createTaskQuery().taskId(taskId).singleResult();
        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        OperationRecord record = new OperationRecord(task, execution);
        record.setId("");
        record.setDuelDate(new Date());
        record.setJumpType(jumpType);
        //添加操作人信息
        wfOperationRecordService.saveRecord(record);
    }
    
    /*------------------回退和撤回的操作 begin ----------------------*/
    /**
     * 回退到指定环节
     * 
     * @param currentTaskID
     * @param destinationTaskID
     * @Description:
     */
    public void rollbackTask(String currentTaskID, String destinationTaskID) {
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
            taskService.complete(current_task.getId());
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
     * 自由挑转
     * 
     * @param currentTaskID
     * @param destinationTaskList
     * @Description:
     */
    public void jumpTask(String currentTaskID, List<String> destinationTaskList) {
        
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
        
        //        List<PvmTransition> newTransitionList = new ArrayList<>();
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
            
        }
        
        try {
            //完成任务
            taskService.complete(current_task.getId());
        } finally {
            // 恢复方向
            // 移除动态添加跳转的线
            //            currActivity.getIncomingTransitions().remove(newTransition);
            //            currActivity.getIncomingTransitions().removeAll(newTransitionList);
            List<PvmTransition> pvmTList = currActivity.getOutgoingTransitions();
            pvmTList.clear();
            pvmTList.addAll(oriPvmTransitionList);
        }
    }
    
    public List<HistoricTaskInstance> getCanRollbackTasks(String currentTaskId) {
        List<HistoricTaskInstance> resultList = new ArrayList<>();
        Task task = taskService.createTaskQuery().taskId(currentTaskId).singleResult();
        List<HistoricTaskInstance> hislist = historyService.createHistoricTaskInstanceQuery()
            .executionId(task.getExecutionId())
            .orderByTaskCreateTime()
            .desc()
            .list();
        String processDefinitionId = task.getProcessDefinitionId();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        List<String> activityIds = new ArrayList<>();
        //取得流程定义
        ProcessDefinitionEntity processDefinition =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
        
        //根据流程定义获取从开始到当前环节的所有活动的id
        ProcessDefinitionUtil
            .getCrossingActivityIds(taskDefinitionKey, processDefinitionId, processDefinition, activityIds);
        //用于过滤已经添加过的记录
        Set<String> checkSet = new HashSet<>();
        if (activityIds.size() > 0) {
            for (HistoricTaskInstance historicTaskInstance : hislist) {
                String task_def_key = historicTaskInstance.getTaskDefinitionKey();
                if (activityIds.contains(task_def_key) && !checkSet.contains(task_def_key)) {
                    resultList.add(historicTaskInstance);
                    checkSet.add(task_def_key);
                }
            }
        }
        return resultList;
        
    }
    
    public boolean canRecall(String taskId) {
        if (wfProcessService.checkProcessIsEnd(taskId)) {
            return false;
        }
        
        HistoricTaskInstance histask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        //        taskId = taskService.createTaskQuery().processInstanceId(histask.getProcessInstanceId());
        return false;
    }
    /*------------------回退和撤回的操作 begin ----------------------*/
}
