package com.zhou.workflowSystem.workflow.testservice.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhou.workflowSystem.workflow.exception.WorkflowException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class baseWorkFlowServiceImpl {
    
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
    
    /**
     * **************************************************************************
     * *****************************流程部署 *************************************
     * **************************************************************************
     */
    
    /**
     * classpath路径部署流程
     * 
     * @param name
     * @param bpmnPath
     * @param pngPath
     * @return
     */
    public Deployment deployByClasspath(String name, String bpmnPath, String pngPath) {
        // 创建部署环境配置对象
        DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
        // 部署流程
        
        // 方式一：读取单个的流程定义文件
        Deployment deployment = deploymentBuilder.name(name) //设置部署流程的名称
            .addClasspathResource(bpmnPath) //设置流程文件
            .addClasspathResource(pngPath) //设置流程文件
            .deploy(); // 部署
        return deployment;
    }
    
    /**
     * zip 压缩文件部署流程
     * 
     * @param name
     * @param zipPath
     * @return
     */
    public Deployment deployByZIP(String name, String zipPath) {
        // 创建部署环境配置对象
        DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
        // 部署流程
        ZipInputStream zipInputStream =
            new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream(zipPath));
        // 方式二：读取zip压缩文件
        Deployment deployment = deploymentBuilder.name(name) //设置部署流程的名称
            .addZipInputStream(zipInputStream)
            .deploy();
        return deployment;
    }
    
    /**
     * 根据模型id部署流程定义（增加流程定义）
     * 
     * @param modelId
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public Deployment deployByModelId(String modelId)
        throws JsonProcessingException, IOException {
        
        //获取模型
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
        
        if (bytes == null) {
            log.error("模型数据为空，请先设计流程并成功保存，再进行发布。");
            return null;
        }
        
        JsonNode modelNode = new ObjectMapper().readTree(bytes);
        
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        if (model.getProcesses().size() == 0) {
            log.error("数据模型不符要求，请至少设计一条主线流程。");
            return null;
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        
        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
            .name(modelData.getName())
            .addString(processName, new String(bpmnBytes, "UTF-8"))
            .deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);
        return deployment;
    }
    
    /**
     * 删除部署
     * 
     * @param deploymentId
     */
    public void delDeployById(String deploymentId) {
        // 设置要删除的部署的ID
        // 删除指定的部署信息，如果有关联信息则报错(例如启动了流程定义相关的流程实例)
        // processEngine.getRepositoryService().deleteDeployment(deploymentId );
        // 删除指定的部署信息，如果有关联信息则级联删除
        // 第二个参数cascade，代表是否级联删除
        processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
    }
    
    /**
     * **************************************************************************
     * * ********************** 流程定义操作 * *************************************
     * **************************************************************************
     */
    
    /**
     * 根据流程定义Key查询最新流程定义.
     *
     * @param processDefinitionKey 流程定义Key
     * @return
     * @throws WorkflowException
     */
    
    public ProcessDefinition findLatestProcessDefinitionByPrcDefKey(String processDefinitionKey)
        throws WorkflowException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(processDefinitionKey)
            .latestVersion()
            .singleResult();
        return processDefinition;
        
    }
    
    /**
     * 根据流程定义Id查询最新流程定义.
     *
     * @param processDefinitionId 流程定义Id
     * @return
     * @throws WorkflowException
     */
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
     * 根据流程定义Id查询流程定义.
     *
     * @param processDefinitionId 流程定义Id
     * @return
     * @throws WorkflowException
     */
    public ProcessDefinitionEntity findProcessDefinitionEntityByProcDefId(String processDefinitionId)
        throws WorkflowException {
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(processDefinitionId);
        return processDefinitionEntity;
    }
    
    /**
     * 删除流程定义，注意：不管流程是否启动，都删除
     * 
     * @param processDefinitionKey
     * @throws WorkflowException
     */
    public void deleteProcessDefinitionByKey(String processDefinitionKey)
        throws WorkflowException {
        // 先使用流程定义的key查询流程定义，查询出所有的版本  
        List<ProcessDefinition> list = processEngine.getRepositoryService()
            .createProcessDefinitionQuery()
            .processDefinitionKey(processDefinitionKey)// 使用流程定义的key查询  
            .list();
        // 遍历，获取每个流程定义的部署ID  
        if (list != null && list.size() > 0) {
            for (ProcessDefinition pd : list) {
                // 获取部署ID  
                String deploymentId = pd.getDeploymentId();
                // 不带级联的删除， 只能删除没有启动的流程，如果流程启动，就会抛出异常  
                // processEngine.getRepositoryService().deleteDeployment(deploymentId);  
                
                /**
                 * 级联删除 不管流程是否启动，都可以删除
                 */
                processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
                
            }
        }
    }
    
    /**
     * 获取流程定义的图片资源
     * 
     * @param filePath
     * @throws IOException
     */
    public void getProcessDefinitionImageResource(String deploymentId, String filePath)
        throws IOException {
        List<String> names = repositoryService.getDeploymentResourceNames(deploymentId);
        String resourceName = null;
        // 遍历资源文件名称列表
        for (String string : names) {
            // 获取'.png'结尾名称为流程图片名称
            if (string.endsWith(".png")) {
                resourceName = string;
            }
        }
        // 如果流程图片存在
        if (resourceName != null) {
            InputStream in = repositoryService.getResourceAsStream(deploymentId, resourceName);
            // 指定拷贝目录
            File file = new File(filePath + resourceName);
            FileUtils.copyInputStreamToFile(in, file);
        }
    }
    
    /**
     * 当前环节高亮显示
     * 
     * @throws IOException
     * 
     */
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
    
    /**
     * **************************************************************************
     * ***************************** 流程实例操作 * ****************************
     * **************************************************************************
     */
    
    /**
     * 根据流程实例Id查询流程实例.
     *
     * @param processInstanceId
     * @return
     * @throws WorkflowException
     */
    public ProcessInstance startProcessInstance(String processDefinitionKey, String businessKey,
        Map<String, Object> variables)
        throws WorkflowException {
        return runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }
    
    /**
     *
     * 根据流程实例Id查询流程实例.
     *
     * @param processInstanceId 流程实例Id
     * @return
     * @throws WorkflowException
     */
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
    public Execution findExecutionByProcInst(String processInstanceId)
        throws WorkflowException {
        return runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult();
    }
    
    /**
     * 根据流程实例Id挂起流程.
     *
     * @param processInstanceId
     * @return
     * @throws WorkflowException
     */
    public void suspendProcessInstanceById(String processInstanceId)
        throws WorkflowException {
        runtimeService.suspendProcessInstanceById(processInstanceId);//挂起流程
    }
    
    /**
     * 根据流程实例Id激活流程.
     *
     * @param processInstanceId
     * @return
     * @throws WorkflowException
     */
    public void activateProcessInstanceById(String processInstanceId)
        throws WorkflowException {
        runtimeService.activateProcessInstanceById(processInstanceId);//激活流程
    }
    
    /**
     * 根据流程实例Id删除流程.
     *
     * @param processInstanceId
     * @return
     * @throws WorkflowException
     */
    public void deleteProcessInstanceById(String processInstanceId, String deleteReason)
        throws WorkflowException {
        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);//激活流程
    }
    
    /**
     * **************************************************************************
     * ***************************** 任务操作 * ****************************
     * **************************************************************************
     */
    
    /**
     * 根据流程实例Id查询任务.
     *
     * @param processInstanceId 流程实例Id
     * @return
     * @throws WorkflowException
     */
    public Task findTaskByProcInstId(String processInstanceId)
        throws WorkflowException {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
    }
    
    /**
     * 根据实例Id查询任务.
     *
     * @param executionId 实例Id
     * @return
     * @throws WorkflowException
     */
    public Task findTaskByExecutionId(String executionId)
        throws WorkflowException {
        return taskService.createTaskQuery().executionId(executionId).singleResult();
    }
    
    /**
     * 查询活动节点
     * 
     * @param taskId
     * @return
     * @throws WorkflowException
     */
    public ActivityImpl findActivity(String taskId)
        throws WorkflowException {
        // 1.获取流程定义
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessDefinitionEntity pd =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        // 2.获取流程实例
        ProcessInstance pi =
            runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        // 3.通过流程实例查找当前活动的ID
        String activitiId = pi.getActivityId();
        // 4.通过活动的ID在流程定义中找到对应的活动对象
        return pd.findActivity(activitiId);
    }
    
    /**
     * 根据活动节点查询任务定义.
     *
     * @param activityImpl 活动节点
     * @return
     * @throws WorkflowException
     */
    public TaskDefinition findTaskDefinitionByActivityImpl(ActivityImpl activityImpl)
        throws WorkflowException {
        return ((UserTaskActivityBehavior)activityImpl.getActivityBehavior()).getTaskDefinition();
    }
    
    /**
     * 查询上一个节点.
     * 
     * @param activityImpl
     * @param activityId
     * @param elString
     * @return
     * @throws WorkflowException
     */
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
     * 根据活动节点、活动线路查询线路的连接线.
     * 
     * @param activityImpl
     * @param transitions
     * @return
     * @throws WorkflowException
     */
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
     * 根据流程定义Id查询任务定义
     *
     * @param processDefinitionId 流程定义Id
     * @return
     * @throws WorkflowException
     */
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
        ;
    }
    
    public void showAllOfProcess(String processInstanceId) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).list().get(0);
        //流程定义
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<Process> processes = bpmnModel.getProcesses();
        /*for (Process process : processes) {
            System.out.println(process.getId());
        }*/
        Process process = processes.get(0);
        //获取所有的FlowElement信息
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            //如果是任务节点
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask)flowElement;
                System.out.println("UserTask ===>> " + userTask.getId());
                //获取入线信息
                List<SequenceFlow> incomingFlows = userTask.getIncomingFlows();
                for (SequenceFlow sequenceFlow : incomingFlows) {
                    System.out.println(sequenceFlow.getId() + "-" + sequenceFlow.getConditionExpression() + "--"
                        + sequenceFlow.getDocumentation() + "-" + sequenceFlow.getSourceRef() + "--"
                        + sequenceFlow.getTargetRef() + "-");
                }
            } else if (flowElement instanceof SequenceFlow) { //流程线
                //                System.out.println(flowElement.getId());
            } else if (flowElement instanceof ParallelGateway) { //并行节点
                ParallelGateway parallelGateway = (ParallelGateway)flowElement;
                //获取入线信息
                List<SequenceFlow> incomingFlows = parallelGateway.getIncomingFlows();
                for (SequenceFlow sequenceFlow : incomingFlows) {
                    System.out.println("ParallelGateway in come flow :" + sequenceFlow);
                }
            } else if (flowElement instanceof EndEvent) { //结束节点
                System.out.println("EndEvent");
            } else {
                
            }
        }
    }
    
    public void test() {
        String taskId = "10008";
        findLastTaskIds(taskId);
    }
    
    public String findLastTaskIds(String taskId) {
        // 1.获取流程定义
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        String processDefinitionId = hisTask.getProcessDefinitionId();
        String processInstanceId = hisTask.getProcessInstanceId();
        ProcessDefinitionEntity pd =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
        // 2.通过活动的ID在流程定义中找到对应的活动对象
        ActivityImpl activityImpl = pd.findActivity(hisTask.getTaskDefinitionKey());
        System.out.println("===>> " + activityImpl.getActivityBehavior());
        //3.递归查询出上一个节点的id
        return findLastTaskId(activityImpl, processInstanceId);
    }
    
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
    
    public void findLastActivityImpl(String taskId) {
        //获取历史任务信息
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        
        //获取流程定义
        ProcessDefinitionEntity pd =
            (ProcessDefinitionEntity)repositoryService.getProcessDefinition(hisTask.getProcessDefinitionId());
        //获取指定得活动信息
        ActivityImpl activityImpl = pd.findActivity(hisTask.getTaskDefinitionKey());
        //获取活动信息得进入线
        List<PvmTransition> inTransitions = activityImpl.getIncomingTransitions(); //
        if (inTransitions != null && inTransitions.size() == 1) {//只有一条线入口
            for (PvmTransition tr : inTransitions) {
                PvmActivity ac = tr.getSource();//获取进入线得来源
                if ("userTask".equals(ac.getProperty("type"))) {
                    HistoricActivityInstance hisActivity =
                        historyService.createHistoricActivityInstanceQuery().activityId(ac.getId()).singleResult();
                    System.out.println(hisActivity.getTaskId());
                }
            }
        }
    }
    
}
