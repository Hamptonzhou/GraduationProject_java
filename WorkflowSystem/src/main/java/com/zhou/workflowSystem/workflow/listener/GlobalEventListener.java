package com.zhou.workflowSystem.workflow.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.EngineServices;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEntityWithVariablesEventImpl;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.Execution;
import org.springframework.stereotype.Component;

import com.zhou.workflowSystem.workflow.constant.WorkflowConstant;
import com.zhou.workflowSystem.workflow.entity.OperationRecord;

@Component
public class GlobalEventListener implements ActivitiEventListener {
    
    private static Map<String, List<TaskEntity>> INST_EVENT_MAP = Collections.synchronizedMap(new HashMap<>());
    
    @Override
    public void onEvent(ActivitiEvent event) {
        System.out.println(event.getType());
        if (event.getType().equals(ActivitiEventType.TASK_COMPLETED)) {//完成事件监听
            //            onTaskCompletedHandler(event);
        } else if (event.getType().equals(ActivitiEventType.TASK_CREATED)) {//任务创建事件
            //            onTaskCreatedHandler(event);
        } else if (event.getType().equals(ActivitiEventType.PROCESS_COMPLETED)) {//任务完成事件
            INST_EVENT_MAP.remove(event.getProcessInstanceId());
        }
        
    }
    
    /**
     * 节点完成处理
     * 
     * @param event
     * @Description:
     */
    private void onTaskCompletedHandler(ActivitiEvent event) {
        ActivitiEntityWithVariablesEventImpl eventImpl = (ActivitiEntityWithVariablesEventImpl)event;
        String executionId = eventImpl.getExecutionId();
        RuntimeService runtimeService = eventImpl.getEngineServices().getRuntimeService();
        Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
        TaskEntity taskEntity = (TaskEntity)eventImpl.getEntity();
        TaskService taskService = eventImpl.getEngineServices().getTaskService();
        String jumpType =
            taskService.getVariableLocal(taskEntity.getId(), WorkflowConstant.ACT_JUMP_TYPE, String.class);
        String lastTaskId =
            taskService.getVariableLocal(taskEntity.getId(), WorkflowConstant.ACT_LAST_TASK_ID, String.class);
        OperationRecord record = new OperationRecord(taskEntity, execution);
        record.setUserId("");
        record.setDuelDate(new Date());
        if (jumpType != null) {
            record.setJumpType(jumpType.toString());
        }
        if (lastTaskId != null) {
            record.setLastTaskId(lastTaskId);
        }
        
        //        IWFOperationRecordService operationRecordService = SpringTool.getBean(IWFOperationRecordService.class);
        //        operationRecordService.saveRecord(record);
        
        //保存到变量中
        String processInstanceId = eventImpl.getProcessInstanceId();
        List<TaskEntity> list = INST_EVENT_MAP.get(processInstanceId);
        if (list == null) {
            list = Collections.synchronizedList(new ArrayList<>());
            INST_EVENT_MAP.put(processInstanceId, list);
        }
        list.add(taskEntity);
        
    }
    
    private void onTaskCreatedHandler(ActivitiEvent event) {
        //类型转换
        ActivitiEntityEventImpl eventImpl = (ActivitiEntityEventImpl)event;
        //拿到具体的节点实体
        TaskEntity taskEntity = (TaskEntity)eventImpl.getEntity();
        
        String processInstanceId = event.getProcessInstanceId();
        EngineServices engineServices = event.getEngineServices();
        TaskService taskService = engineServices.getTaskService();
        HistoryService historyService = engineServices.getHistoryService();
        String lastTaskId = null;//上一次任务的id
        //获取当前环节的入口线
        List<PvmTransition> inTransitions = getIncomTransitions(taskEntity, engineServices);
        
        if (checkLastIsStart(inTransitions)) {
            lastTaskId = "start";
            taskService.setVariableLocal(taskEntity.getId(), WorkflowConstant.ACT_LAST_TASK_ID, lastTaskId);
            return;
        }
        
        //递归查询出指定节点的activityid
        List<String> activityIds = new ArrayList<>();
        getListTaskList(inTransitions, activityIds);
        StringBuffer sBuffer = new StringBuffer();
        if (activityIds.size() > 0) {
            //根据activityid 查询出taskId
            for (String activityId : activityIds) {
                List<HistoricActivityInstance> hisActivityList = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .activityId(activityId)
                    .orderByHistoricActivityInstanceStartTime()
                    .desc()
                    .list();
                if (hisActivityList != null && hisActivityList.size() > 0) {
                    sBuffer.append("," + hisActivityList.get(0).getTaskId());
                }
            }
            if (sBuffer.length() > 0) {
                lastTaskId = sBuffer.substring(1);
            }
            taskService.setVariableLocal(taskEntity.getId(), WorkflowConstant.ACT_LAST_TASK_ID, lastTaskId);
        }
        
    }
    
    private List<PvmTransition> getIncomTransitions(TaskEntity taskEntity, EngineServices engineServices) {
        RepositoryService repositoryService = engineServices.getRepositoryService();
        //1.获取流程定义
        ProcessDefinitionEntity pd =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(taskEntity.getProcessDefinitionId());
        // 2.通过活动的ID在流程定义中找到对应的活动对象
        ActivityImpl activityImpl = pd.findActivity(taskEntity.getTaskDefinitionKey());
        //3.入口线路
        return activityImpl.getIncomingTransitions(); //通过活动节点查询所有线路
    }
    
    //
    private boolean checkLastIsStart(List<PvmTransition> inTransitions) {
        for (PvmTransition tr : inTransitions) {
            PvmActivity ac = tr.getSource();//获取进入线得来源
            if ("startEvent".equals(ac.getProperty("type"))) {//开始
                return true;
            }
        }
        return false;
    }
    
    private void getListTaskList(List<PvmTransition> inTransitions, List<String> activityIds) {
        for (PvmTransition tr : inTransitions) {
            PvmActivity ac = tr.getSource();//获取进入线得来源
            String type = ac.getProperty("type").toString();
            if (type.indexOf("Gateway") >= 0) {
                List<PvmTransition> ch_pvmTransitions = ac.getIncomingTransitions();
                for (PvmTransition ch_tr : ch_pvmTransitions) {
                    PvmActivity ch_ac = ch_tr.getSource();//获取进入线得来源
                    if ("userTask".equals(ch_ac.getProperty("type"))) {
                        activityIds.add(ch_ac.getId());
                    } else {
                        getListTaskList(inTransitions, activityIds);
                    }
                }
            } else if ("userTask".equals(type)) {
                activityIds.add(ac.getId());
            }
        }
    }
    
    @Override
    public boolean isFailOnException() {
        // TODO Auto-generated method stub
        return false;
    }
    
}
