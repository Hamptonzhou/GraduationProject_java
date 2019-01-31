package com.southsmart.webplat.workflow.testservice.impl;

import java.util.Iterator;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.southsmart.webplat.workflow.exception.WorkflowException;
import com.southsmart.webplat.workflow.testservice.BaseProcessService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BaseProcessServiceImpl implements BaseProcessService {
    /**
     * 日志打印
     */
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private HistoryService historyService;
    
    /**
     * 方法说明 ： 根据流程定义Key查询最新流程定义.
     *
     * @param processDefinitionKey 流程定义Key
     * @return
     * @throws WorkflowException
     */
    @Override
    public ProcessDefinition findLatestProcessDefinitionByPrcDefKey(String processDefinitionKey)
        throws WorkflowException {
        
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(processDefinitionKey)
            .latestVersion()
            .singleResult();
        return processDefinition;
        
    }
    
    /**
     * 方法说明 ： 根据流程定义Id查询最新流程定义.
     *
     * @param processDefinitionId 流程定义Id
     * @return
     * @throws WorkflowException
     */
    @Override
    public ProcessDefinition findProcessDefinitionByPrcDefId(String processDefinitionId)
        throws WorkflowException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(processDefinitionId)
            .orderByProcessDefinitionVersion()
            .desc()
            .singleResult();
        
        return processDefinition;
    }
    
    /**
     * 方法说明 ： 根据流程定义Id查询流程定义.
     *
     * @param processDefinitionId 流程定义Id
     * @return
     * @throws WorkflowException
     */
    @Override
    public ProcessDefinitionEntity findProcessDefinitionEntityByProcDefId(String processDefinitionId)
        throws WorkflowException {
        
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(processDefinitionId);
        return processDefinitionEntity;
    }
    
    /**
     *
     * 方法说明 ： 根据流程实例Id查询流程实例.
     *
     * @param processInstanceId 流程实例Id
     * @return
     * @throws WorkflowException
     */
    @Override
    public ProcessInstance findProcessInstanceByProcInst(String processInstanceId)
        throws WorkflowException {
        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }
    
    /**
     * 根据流程实例Id查询流程实例.
     *
     * @param processInstanceId
     * @return
     * @throws WorkflowException
     */
    @Override
    public Execution findExecutionByProcInst(String processInstanceId)
        throws WorkflowException {
        return runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult();
    }
    
    /**
     * 方法说明 ： 根据流程实例Id查询任务.
     *
     * @param processInstanceId 流程实例Id
     * @return
     * @throws WorkflowException
     */
    @Override
    public Task findTaskByProcInstId(String processInstanceId)
        throws WorkflowException {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
    }
    
    /**
     * 方法说明 ： 根据实例Id查询任务.
     *
     * @param executionId 实例Id
     * @return
     * @throws WorkflowException
     */
    @Override
    public Task findTaskByExecutionId(String executionId)
        throws WorkflowException {
        return taskService.createTaskQuery().executionId(executionId).singleResult();
    }
    
    /**
     * 方法说明 ： 根据活动节点查询任务定义.
     *
     * @param activityImpl 活动节点
     * @return
     * @throws WorkflowException
     */
    @Override
    public TaskDefinition findTaskDefinitionByActivityImpl(ActivityImpl activityImpl)
        throws WorkflowException {
        return ((UserTaskActivityBehavior)activityImpl.getActivityBehavior()).getTaskDefinition();
    }
    
    /**
     * 方法说明 : 查询上一个节点.
     *
     * @param activityImpl 活动节点
     * @param activityId 当前活动节点ID
     * @param elString
     * @return
     * @throws ShineException
     */
    @Override
    public TaskDefinition beforeTaskDefinition(ActivityImpl activityImpl, String activityId, String elString)
        throws WorkflowException {
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            TaskDefinition taskDefinition = null;
            if (activityImpl != null) {
                taskDefinition = findTaskDefinitionByActivityImpl(activityImpl);
            }
            return taskDefinition;
        } else {
            List<PvmTransition> inTransitions = activityImpl.getIncomingTransitions(); //通过活动节点查询所有线路
            if (inTransitions != null && !inTransitions.isEmpty()) {
                List<PvmTransition> inTransitionsTemp = null;
                for (PvmTransition tr : inTransitions) {
                    PvmActivity ac = tr.getSource(); //获取线路的前节点   
                    if ("exclusiveGateway".equals(ac.getProperty("type"))) {
                        inTransitionsTemp = ac.getIncomingTransitions();
                        if (inTransitionsTemp.size() == 1) {
                            return beforeTaskDefinition((ActivityImpl)inTransitionsTemp.get(0).getSource(),
                                activityId,
                                elString);
                        } else if (inTransitionsTemp.size() > 1) {
                            for (PvmTransition tr1 : inTransitionsTemp) {
                                Object s = tr1.getProperty("conditionText");
                                String str_s = s != null ? s.toString().trim().replace(" ", "") : null;
                                if (elString.equals(str_s)) {
                                    return beforeTaskDefinition((ActivityImpl)tr1.getSource(), activityId, elString);
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
    }
    
    /**
     * 方法说明 : 查询下一个节点.
     *
     * @param activityImpl 活动节点
     * @param activityId 当前活动节点ID
     * @param elString
     * @return
     * @throws ShineException
     */
    @Override
    public TaskDefinition nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString)
        throws WorkflowException {
        
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            TaskDefinition taskDefinition = null;
            if (activityImpl != null) {
                taskDefinition = findTaskDefinitionByActivityImpl(activityImpl);
            }
            return taskDefinition;
        } else {
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions(); //通过活动节点查询所有线路
            if (outTransitions != null && !outTransitions.isEmpty()) {
                List<PvmTransition> outTransitionsTemp = null;
                for (PvmTransition tr : outTransitions) {
                    PvmActivity ac = tr.getDestination(); //获取线路的终点节点   
                    if ("exclusiveGateway".equals(ac.getProperty("type"))) {
                        outTransitionsTemp = ac.getOutgoingTransitions();
                        if (outTransitionsTemp.size() == 1) {
                            return nextTaskDefinition((ActivityImpl)outTransitionsTemp.get(0).getDestination(),
                                activityId,
                                elString);
                        } else if (outTransitionsTemp.size() > 1) {
                            for (PvmTransition tr1 : outTransitionsTemp) {
                                Object s = tr1.getProperty("conditionText");
                                String str_s = s != null ? s.toString().trim().replace(" ", "") : null;
                                if (s != null && elString.equals(str_s)) {
                                    return nextTaskDefinition((ActivityImpl)tr1.getDestination(), activityId, elString);
                                }
                            }
                        }
                    } else if ("userTask".equals(ac.getProperty("type"))) {
                        return findTaskDefinitionByActivityImpl((ActivityImpl)ac);
                    } else if ("startEvent".equals(ac.getProperty("type"))) {
                        return findTaskDefinitionByActivityImpl((ActivityImpl)ac);
                    } else {
                        log.info(ac.getProperty("type").toString());
                    }
                }
            }
            return null;
        }
        
    }
    
    /**
     * 方法说明： 根据活动节点、活动线路查询线路的连接线.
     *
     * @return
     * @throws WorkflowException
     */
    @SuppressWarnings("rawtypes")
    @Override
    public PvmActivity findPvmActivity(ActivityImpl activityImpl, String transitions)
        throws WorkflowException {
        
        PvmActivity activity = null;
        List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions(); //获取所有线路
        
        for (Iterator iterator = pvmTransitions.iterator(); iterator.hasNext();) {
            PvmTransition pvmTransition = (PvmTransition)iterator.next();
            PvmActivity pvmActivity = pvmTransition.getDestination(); //获取下一个任务节点
            String transitionsVal = (String)pvmActivity.getProperty("name");
            if (transitions.equals(transitionsVal)) {
                activity = pvmActivity;
                break;
            }
        }
        return activity;
    }
    
    /**
     * 方法说明 ：根据流程定义Id查询任务定义
     *
     * @param processDefinitionId 流程定义Id
     * @return
     * @throws WorkflowException
     */
    @Override
    public TaskDefinition findTaskDefinition(String processDefinitionId)
        throws WorkflowException {
        
        //获取流程定义
        ProcessDefinitionEntity processDefinitionEntity = findProcessDefinitionEntityByProcDefId(processDefinitionId);
        TaskDefinition tdf = null;
        
        if (processDefinitionEntity != null) {
            List<ActivityImpl> activityImpls = processDefinitionEntity.getActivities(); //获取所有活动的节点
            for (int i = activityImpls.size() - 1; i > 0; i--) {
                ActivityImpl activityImpl = activityImpls.get(i);
                String startEventType = (String)activityImpl.getProperty("type");
                if ("startEvent".equals(startEventType)) {
                    tdf = nextTaskDefinition(activityImpl, activityImpl.getId(), null);
                }
            }
        }
        return tdf;
    }
    
    /**
     * 方法说明 ： 添加任务意见.
     *
     * @param taskId 任务Id
     * @param processInstanceId 流程实例Id
     * @param comment 意见
     * @throws WorkflowException
     */
    @Override
    public void addTaskComment(String taskId, String processInstanceId, String comment)
        throws WorkflowException {
        taskService.addComment(taskId, processInstanceId, comment);
    }
    
    /**
     * 方法说明 ： 拾取任务.
     *
     * @param taskId 任务Id
     * @param operator 办理人
     * @throws WorkflowException
     */
    @Override
    public void claimTask(String taskId, String operator)
        throws WorkflowException {
        taskService.claim(taskId, operator);
    }
    
}
