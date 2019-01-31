package com.southsmart.webplat.workflow.util;

import java.util.List;

import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import com.southsmart.webplat.workflow.constant.WorkflowConstant;

public class ProcessDefinitionUtil {
    
    /**
     * 检查下一个环节是否结束节点
     * 
     * @param taskDefinitionKey 任务定义的id，同activityId
     * @param processDefinitionId 流程定义的id
     * @param processDefinition 流程定义实体，从repositoryService获取
     * @return
     * @Description:
     */
    public static boolean checkNextActivityIsEnd(String taskDefinitionKey, String processDefinitionId,
        ProcessDefinitionEntity processDefinition) {
        List<PvmTransition> outTransitions =
            getOutgoingTransitions(taskDefinitionKey, processDefinitionId, processDefinition);
        for (PvmTransition tr : outTransitions) {
            PvmActivity destination = tr.getDestination();//获取离开线的目的地
            if (WorkflowConstant.ACT_ACTIVITY_TYPE_END_EVENT
                .equals(destination.getProperty(WorkflowConstant.ACT_PVMACTIVITY_PROPERTY_TYPE))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查上一个环节是否开始节点
     * 
     * @param taskDefinitionKey 任务定义的id，同activityId
     * @param processDefinitionId 流程定义的id
     * @param processDefinition 流程定义实体，从repositoryService获取
     * @return
     * @Description:
     */
    public static boolean checkLastActivityIsStart(String taskDefinitionKey, String processDefinitionId,
        ProcessDefinitionEntity processDefinition) {
        List<PvmTransition> inTransitions =
            getIncomingTransitions(taskDefinitionKey, processDefinitionId, processDefinition);
        for (PvmTransition tr : inTransitions) {
            PvmActivity source = tr.getSource();//获取进入线得来源
            if (WorkflowConstant.ACT_ACTIVITY_TYPE_START_EVENT
                .equals(source.getProperty(WorkflowConstant.ACT_PVMACTIVITY_PROPERTY_TYPE))) {//开始
                return true;
            }
        }
        return false;
    }
    
    /**
     * 根据流程线路递归查询下一活动的id
     * 
     * @param taskDefinitionKey 任务定义的id，同activityId
     * @param processDefinitionId 流程定义的id
     * @param processDefinition 流程定义实体，从repositoryService获取
     * @param outputList 结果输出
     * @Description:
     */
    public static void getNextActivityIds(String taskDefinitionKey, String processDefinitionId,
        ProcessDefinitionEntity processDefinition, List<String> outputList) {
        List<PvmTransition> outTransitions =
            getOutgoingTransitions(taskDefinitionKey, processDefinitionId, processDefinition);
        for (PvmTransition tr : outTransitions) {
            PvmActivity destination = tr.getDestination();//获取进入线得来源
            String type = destination.getProperty(WorkflowConstant.ACT_PVMACTIVITY_PROPERTY_TYPE).toString();
            if (type.indexOf(WorkflowConstant.ACT_ACTIVITY_TYPE_GATEWAY) >= 0) {
                List<PvmTransition> ch_pvmTransitions = destination.getOutgoingTransitions();
                for (PvmTransition ch_tr : ch_pvmTransitions) {
                    PvmActivity ch_destination = ch_tr.getDestination();//获取进入线得来源
                    if (WorkflowConstant.ACT_ACTIVITY_TYPE_USER_TASK
                        .equals(ch_destination.getProperty(WorkflowConstant.ACT_PVMACTIVITY_PROPERTY_TYPE))) {
                        outputList.add(ch_destination.getId());
                    } else {
                        getNextActivityIds(taskDefinitionKey, processDefinitionId, processDefinition, outputList);
                    }
                }
            } else if (WorkflowConstant.ACT_ACTIVITY_TYPE_USER_TASK.equals(type)) {
                outputList.add(destination.getId());
            }
        }
    }
    
    public static void getCrossingActivityIds(String taskDefinitionKey, String processDefinitionId,
        ProcessDefinitionEntity processDefinition, List<String> outputList) {
        List<PvmTransition> inTransitions =
            getIncomingTransitions(taskDefinitionKey, processDefinitionId, processDefinition);
        for (PvmTransition tr : inTransitions) {
            PvmActivity source = tr.getSource();//获取进入线得来源
            String type = source.getProperty(WorkflowConstant.ACT_PVMACTIVITY_PROPERTY_TYPE).toString();
            if (WorkflowConstant.ACT_ACTIVITY_TYPE_USER_TASK.equals(type)) {
                outputList.add(source.getId());
            } else if (WorkflowConstant.ACT_ACTIVITY_TYPE_START_EVENT.equals(type)) {
                return;
            } else {
                getLastActivityIds(inTransitions, outputList);
            }
        }
    }
    
    /**
     * 根据流程线路递归查询下一个活动的id
     * 
     * @param taskDefinitionKey 任务定义的id，同activityId
     * @param processDefinitionId 流程定义的id
     * @param processDefinition 流程定义实体，从repositoryService获取
     * @param outputList 结果输出
     * @Description:
     */
    public static void getLastActivityIds(List<PvmTransition> inTransitions, List<String> outputList) {
        for (PvmTransition tr : inTransitions) {
            PvmActivity source = tr.getSource();//获取进入线得来源
            String type = source.getProperty(WorkflowConstant.ACT_PVMACTIVITY_PROPERTY_TYPE).toString();
            if (type.indexOf(WorkflowConstant.ACT_ACTIVITY_TYPE_GATEWAY) >= 0) {
                List<PvmTransition> ch_pvmTransitions = source.getIncomingTransitions();
                for (PvmTransition ch_tr : ch_pvmTransitions) {
                    PvmActivity ch_source = ch_tr.getSource();//获取进入线得来源
                    if (WorkflowConstant.ACT_ACTIVITY_TYPE_USER_TASK
                        .equals(ch_source.getProperty(WorkflowConstant.ACT_PVMACTIVITY_PROPERTY_TYPE))) {
                        outputList.add(ch_source.getId());
                    } else {
                        getLastActivityIds(inTransitions, outputList);
                    }
                }
            } else if (WorkflowConstant.ACT_ACTIVITY_TYPE_USER_TASK.equals(type)) {
                outputList.add(source.getId());
            }
        }
    }
    
    /**
     * 获取指定节点的进入线信息
     * 
     * @param taskDefinitionKey 任务定义的id，同activityId
     * @param processDefinitionId 流程定义的id
     * @param processDefinition 流程定义实体，从repositoryService获取
     * @return
     * @Description:
     */
    private static List<PvmTransition> getIncomingTransitions(String taskDefinitionKey, String processDefinitionId,
        ProcessDefinitionEntity processDefinition) {
        //通过活动的ID在流程定义中找到对应的活动对象
        ActivityImpl activityImpl = processDefinition.findActivity(taskDefinitionKey);
        //入口线路
        return activityImpl.getIncomingTransitions(); //通过活动节点查询所有线路
    }
    
    /**
     * 获取指定节点的离开线信息
     * 
     * @param taskDefinitionKey 任务定义的id，同activityId
     * @param processDefinitionId 流程定义的id
     * @param processDefinition 流程定义实体，从repositoryService获取
     * @return
     * @Description:
     */
    private static List<PvmTransition> getOutgoingTransitions(String taskDefinitionKey, String processDefinitionId,
        ProcessDefinitionEntity processDefinition) {
        // 通过活动的ID在流程定义中找到对应的活动对象
        ActivityImpl activityImpl = processDefinition.findActivity(taskDefinitionKey);
        //离开线路
        return activityImpl.getOutgoingTransitions(); //通过活动节点查询所有线路
    }
}
