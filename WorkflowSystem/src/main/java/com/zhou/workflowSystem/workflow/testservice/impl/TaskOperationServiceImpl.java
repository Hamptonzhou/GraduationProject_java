package com.zhou.workflowSystem.workflow.testservice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

import com.zhou.workflowSystem.workflow.exception.WorkflowException;

public class TaskOperationServiceImpl {
    
    @Autowired
    ProcessEngine processEngine;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private HistoryService historyService;
    
    @Autowired
    private ManagementService managementService;
    
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
     * 拾取任务.
     *
     * @param taskId 任务Id
     * @param operator 办理人
     * @throws WorkflowException
     */
    public void claimTask(String taskId, String operator)
        throws WorkflowException {
        taskService.claim(taskId, operator);
    }
    
    /**
     * 指派任务.
     *
     * @param taskId 任务Id
     * @param operator 办理人
     * @throws WorkflowException
     */
    public void assigneeTask(String taskId, String operator)
        throws WorkflowException {
        taskService.setAssignee(taskId, operator);
    }
    
    /**
     * 完成任务.
     *
     * @param taskId 任务Id
     * @param operator 办理人
     * @throws WorkflowException
     */
    public void completeTask(String taskId, Map<String, Object> variables)
        throws WorkflowException {
        taskService.complete(taskId, variables);
    }
    
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
        Task now_task = taskService.createTaskQuery().taskId(currentTaskID).singleResult();
        //取得当前待办任务的流程实例id
        //        String executionId = now_task.getExecutionId();
        String executionId = now_task.getProcessInstanceId();
        //取得流程定义
        ProcessDefinitionEntity definition =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(hisTask.getProcessDefinitionId());
        //获取历史任务的Activity
        ActivityImpl hisActivity = definition.findActivity(hisTask.getTaskDefinitionKey());
        //实现跳转
        managementService.executeCommand(new JumpCmd(executionId, hisActivity.getId()));
    }
    
    /**
     * 将节点之后的节点删除然后指向新的节点。
     * 
     * @param actDefId 流程定义ID
     * @param nodeId 流程节点ID
     * @param aryDestination 需要跳转的节点
     * @return Map<String,Object> 返回节点和需要恢复节点的集合。
     */
    private Map<String, Object> prepare(String actDefId, String nodeId, String[] aryDestination) {
        Map<String, Object> map = new HashMap<String, Object>();
        
        //修改流程定义  
        ProcessDefinitionEntity processDefinition =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(actDefId);
        
        ActivityImpl curAct = processDefinition.findActivity(nodeId);
        List<PvmTransition> outTrans = curAct.getOutgoingTransitions();
        List<PvmTransition> cloneOutTrans = new ArrayList<>();
        cloneOutTrans.addAll(outTrans);
        map.put("outTrans", cloneOutTrans);
        
        /**
         * 解决通过选择自由跳转指向同步节点导致的流程终止的问题。 在目标节点中删除指向自己的流转。
         */
        for (Iterator<PvmTransition> it = outTrans.iterator(); it.hasNext();) {
            PvmTransition transition = it.next();
            PvmActivity activity = transition.getDestination();
            List<PvmTransition> inTrans = activity.getIncomingTransitions();
            for (Iterator<PvmTransition> itIn = inTrans.iterator(); itIn.hasNext();) {
                PvmTransition inTransition = itIn.next();
                if (inTransition.getSource().getId().equals(curAct.getId())) {
                    itIn.remove();
                }
            }
        }
        
        curAct.getOutgoingTransitions().clear();
        
        if (aryDestination != null && aryDestination.length > 0) {
            for (String dest : aryDestination) {
                //创建一个连接  
                ActivityImpl destAct = processDefinition.findActivity(dest);
                TransitionImpl transitionImpl = curAct.createOutgoingTransition();
                transitionImpl.setDestination(destAct);
            }
        }
        
        map.put("activity", curAct);
        
        return map;
        
    }
    
    /**
     * 通过指定目标节点，实现任务的跳转
     * 
     * @param taskId 任务ID
     * @param destNodeIds 跳至的目标节点ID
     */
    public synchronized void completeTask(String taskId, String[] destNodeIds) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        
        String curNodeId = task.getTaskDefinitionKey();
        String actDefId = task.getProcessDefinitionId();
        
        //        Map<String, Object> activityMap = prepare(actDefId, curNodeId, destNodeIds);
        Map<String, Object> map = new HashMap<String, Object>();
        
        //修改流程定义  
        ProcessDefinitionEntity processDefinition =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(actDefId);
        
        ActivityImpl curAct = processDefinition.findActivity(curNodeId);
        List<PvmTransition> outTrans = curAct.getOutgoingTransitions();
        List<PvmTransition> cloneOutTrans = new ArrayList<>();
        cloneOutTrans.addAll(outTrans);
        map.put("outTrans", cloneOutTrans);
        
        /**
         * 解决通过选择自由跳转指向同步节点导致的流程终止的问题。 在目标节点中删除指向自己的流转。
         */
        for (Iterator<PvmTransition> it = outTrans.iterator(); it.hasNext();) {
            PvmTransition transition = it.next();
            PvmActivity activity = transition.getDestination();
            List<PvmTransition> inTrans = activity.getIncomingTransitions();
            for (Iterator<PvmTransition> itIn = inTrans.iterator(); itIn.hasNext();) {
                PvmTransition inTransition = itIn.next();
                if (inTransition.getSource().getId().equals(curAct.getId())) {
                    itIn.remove();
                }
            }
        }
        
        curAct.getOutgoingTransitions().clear();
        
        if (destNodeIds != null && destNodeIds.length > 0) {
            for (String dest : destNodeIds) {
                //创建一个连接  
                ActivityImpl destAct = processDefinition.findActivity(dest);
                TransitionImpl transitionImpl = curAct.createOutgoingTransition();
                transitionImpl.setDestination(destAct);
            }
        }
        
        map.put("activity", curAct);
        
        try {
            taskService.complete(taskId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            //恢复  
            curAct.getOutgoingTransitions().clear();
            curAct.getOutgoingTransitions().addAll(outTrans);
        }
    }
    
    /**
     * 通过指定目标节点，实现任务的跳转
     * 
     * @param taskId 任务ID
     * @param destNodeIds 跳至的目标节点ID
     */
    /*public synchronized void completeTask(String taskId, String[] destNodeIds) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        
        String curNodeId = task.getTaskDefinitionKey();
        String actDefId = task.getProcessDefinitionId();
        
        //修改流程定义  
        ProcessDefinitionEntity processDefinition =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(actDefId);
        
        ActivityImpl curAct = processDefinition.findActivity(curNodeId);
        List<PvmTransition> outTrans = curAct.getOutgoingTransitions();
        List<PvmTransition> cloneOutTrans = null;
        try {
            cloneOutTrans = (List<PvmTransition>)CloneUtil.deepClone(outTrans);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        *//**
           * 解决通过选择自由跳转指向同步节点导致的流程终止的问题。 在目标节点中删除指向自己的流转。
           *//*
             for (Iterator<PvmTransition> it = outTrans.iterator(); it.hasNext();) {
              PvmTransition transition = it.next();
              PvmActivity activity = transition.getDestination();
              List<PvmTransition> inTrans = activity.getIncomingTransitions();
              for (Iterator<PvmTransition> itIn = inTrans.iterator(); itIn.hasNext();) {
                  PvmTransition inTransition = itIn.next();
                  if (inTransition.getSource().getId().equals(curAct.getId())) {
                      itIn.remove();
                  }
              }
             }
             
             curAct.getOutgoingTransitions().clear();
             
             if (destNodeIds != null && destNodeIds.length > 0) {
              for (String dest : destNodeIds) {
                  //创建一个连接  
                  ActivityImpl destAct = processDefinition.findActivity(dest);
                  TransitionImpl transitionImpl = curAct.createOutgoingTransition();
                  transitionImpl.setDestination(destAct);
              }
             }
             
             try {
              taskService.complete(taskId);
             } catch (Exception ex) {
              throw new RuntimeException(ex);
             } finally {
              //恢复  
              curAct.getOutgoingTransitions().clear();
              curAct.getOutgoingTransitions().addAll(cloneOutTrans);
             }
             }*/
    
    public String findLastTaskId(ActivityImpl activityImpl, String processInstanceId) {
        List<PvmTransition> inTransitions = activityImpl.getIncomingTransitions(); //通过活动节点查询所有线路
        if (inTransitions != null && !inTransitions.isEmpty()) {
            if (inTransitions != null && inTransitions.size() == 1) {//只有一条线入口
                PvmTransition tr = inTransitions.get(0);
                PvmActivity ac = tr.getSource();//获取进入线得来源
                if ("userTask".equals(ac.getProperty("type"))) {
                    HistoricActivityInstance hisActivity = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .activityId(ac.getId())
                        .orderByHistoricActivityInstanceStartTime()
                        .desc()
                        .singleResult();
                    return hisActivity.getTaskId();
                } else if ("parallelGateway".equals(ac.getProperty("type"))) {
                    findLastTaskId((ActivityImpl)ac, processInstanceId);
                }
                
            }
            if (inTransitions.size() > 1) {
                StringBuffer tasks = new StringBuffer();
                for (PvmTransition tr : inTransitions) {
                    PvmActivity ac = tr.getSource();//获取进入线得来源
                    if ("userTask".equals(ac.getProperty("type"))) {
                        HistoricActivityInstance hisActivity = historyService.createHistoricActivityInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .activityId(ac.getId())
                            .orderByHistoricActivityInstanceStartTime()
                            .desc()
                            .singleResult();
                        if (tasks.length() > 0) {
                            tasks.append("," + hisActivity.getTaskId());
                        } else {
                            tasks.append(hisActivity.getTaskId());
                        }
                    } else if ("parallelGateway".equals(ac.getProperty("type"))) {
                        findLastTaskId((ActivityImpl)ac, processInstanceId);
                    } else {
                        //其他类型处理
                    }
                }
                return tasks.toString();
            }
        }
        return null;
    }
}
