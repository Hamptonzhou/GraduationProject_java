package com.southsmart.webplat.workflow.testservice.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.southsmart.webplat.workflow.dao.LeaveDao;
import com.southsmart.webplat.workflow.entity.Leave;
import com.southsmart.webplat.workflow.model.CustomActivitiTask;
import com.southsmart.webplat.workflow.model.CustomProcessDefinition;
import com.southsmart.webplat.workflow.model.CustomProcessInstance;

@Service
public class LeaveService {
    
    @Autowired
    ProcessEngine processEngine;
    
    @Autowired
    private IdentityService identityService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private LeaveDao leaveDao;
    
    @Autowired
    private HistoryService historyService;
    
    /**
     * 保存请假实体并启动流程
     */
    public ProcessInstance startWorkflow(Leave entity, String userId, Map<String, Object> variables,
        String processDefinitionKey) {
        if (entity.getId() == null) {
            entity.setApplyTime(new Date());
            entity.setUserId(userId);
        }
        leaveDao.save(entity);
        String businessKey = entity.getId().toString();
        
        // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        //        identityService.setAuthenticatedUserId(userId);
        
        ProcessInstance processInstance =
            runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        String processInstanceId = processInstance.getId();
        entity.setProcessInstanceId(processInstanceId);
        leaveDao.save(entity);
        return new CustomProcessInstance(processInstance);
    }
    
    /**
     * 查询待办任务
     */
    public List<Leave> findTodoTasks(String userId, String processDefinitionKey) {
        List<Leave> results = new ArrayList<Leave>();
        List<Task> tasks = new ArrayList<Task>();
        // 根据当前人的ID查询
        List<Task> todoList =
            taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskAssignee(userId).list();
        
        // 根据当前人未签收的任务
        List<Task> unsignedTasks =
            taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskCandidateUser(userId).list();
        
        // 合并
        tasks.addAll(todoList);
        tasks.addAll(unsignedTasks);
        
        // 根据流程的业务ID查询实体并关联
        for (Task task : tasks) {
            String processInstanceId = task.getProcessInstanceId();
            System.out.println(task.toString());
            ProcessInstance processInstance =
                runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String businessKey = processInstance.getBusinessKey();
            Leave leave = leaveDao.getOne(new Long(businessKey));
            leave.setTask(new CustomActivitiTask(task));
            leave.setProcessInstance(new CustomProcessInstance(processInstance));
            String processDefinitionId = processInstance.getProcessDefinitionId();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
            leave.setProcessDefinition(new CustomProcessDefinition(processDefinition));
            results.add(leave);
        }
        return results;
    }
    
    public void complete(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }
    
    public Leave get(String id) {
        Leave leave = leaveDao.getOne(new Long(id));
        return leave;
    }
    
    public void getDueTaskHignlightImage(String processInstanceId, String filePath)
        throws IOException {
        
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        //流程定义
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        
        //正在活动节点
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(task.getExecutionId());
        
        ProcessDiagramGenerator pdg = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
        //生成流图片
        InputStream inputStream = pdg.generateDiagram(bpmnModel,
            "PNG",
            activeActivityIds,
            activeActivityIds,
            processEngine.getProcessEngineConfiguration().getActivityFontName(),
            processEngine.getProcessEngineConfiguration().getLabelFontName(),
            processEngine.getProcessEngineConfiguration().getActivityFontName(),
            processEngine.getProcessEngineConfiguration().getProcessEngineConfiguration().getClassLoader(),
            1.0);
        //生成本地图片
        String pathName = filePath + processInstanceId + ".png";
        File file = new File(pathName);
        FileUtils.copyInputStreamToFile(inputStream, file);
    }
    
    /**
     * 方法一：生成流程图；带进度:<br>
     * 得到带有高亮节点的流程图
     * 
     * @param processInstanceId 流程实例id
     * @return
     * @throws IOException
     */
    public void traceProcessImage(String processInstanceId, String filePath)
        throws IOException {
        //获取历史流程实例
        HistoricProcessInstance processInstance =
            historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl)processEngineConfiguration);
        
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        ProcessDefinitionEntity definitionEntity =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
        
        List<HistoricActivityInstance> highLightedActivitList =
            historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        //高亮环节id集合
        List<String> highLightedActivitis = new ArrayList<String>();
        //高亮线路id集合
        List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);
        
        for (HistoricActivityInstance tempActivity : highLightedActivitList) {
            String activityId = tempActivity.getActivityId();
            highLightedActivitis.add(activityId);
        }
        
        //中文显示的是口口口，设置字体就好了
        InputStream inputStream = diagramGenerator.generateDiagram(bpmnModel,
            "png",
            highLightedActivitis,
            highLightedFlows,
            processEngine.getProcessEngineConfiguration().getActivityFontName(),
            processEngine.getProcessEngineConfiguration().getLabelFontName(),
            processEngine.getProcessEngineConfiguration().getActivityFontName(),
            processEngine.getProcessEngineConfiguration().getProcessEngineConfiguration().getClassLoader(),
            1.0);
        //单独返回流程图，不高亮显示
        //        InputStream imageStream = diagramGenerator.generatePngDiagram(bpmnModel);
        //生成本地图片
        String pathName = filePath + processInstanceId + ".png";
        File file = new File(pathName);
        FileUtils.copyInputStreamToFile(inputStream, file);
    }
    
    /**
     * 获取需要高亮的线
     * 
     * @param processDefinitionEntity
     * @param historicActivityInstances
     * @return
     */
    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity,
        List<HistoricActivityInstance> historicActivityInstances) {
        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
            ActivityImpl activityImpl =
                processDefinitionEntity.findActivity(historicActivityInstances.get(i).getActivityId());// 得到节点定义的详细信息
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
            ActivityImpl sameActivityImpl1 =
                processDefinitionEntity.findActivity(historicActivityInstances.get(i + 1).getActivityId());
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// 后续第二个节点
                if (activityImpl1.getStartTime().equals(activityImpl2.getStartTime())) {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 =
                        processDefinitionEntity.findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    // 有不相同跳出循环
                    break;
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();// 取出节点的所有出去的线
            for (PvmTransition pvmTransition : pvmTransitions) {
                // 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl)pvmTransition.getDestination();
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }
    
}
