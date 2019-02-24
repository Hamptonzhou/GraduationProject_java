package com.zhou.workflowSystem.workflow.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhou.utils.PageQueryData;
import com.zhou.workflowSystem.workflow.entity.MyWorkEntity;
import com.zhou.workflowSystem.workflow.entity.ProcessDefinitionTree;
import com.zhou.workflowSystem.workflow.model.CustomActivitiTask;
import com.zhou.workflowSystem.workflow.model.CustomProcessInstance;
import com.zhou.workflowSystem.workflow.service.ICustomService;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月6日
 * @Version:1.1.0
 */
@Service
public class CustomServiceImpl implements ICustomService<MyWorkEntity> {
    //@Autowired
    //private CustomDao customDao;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private HistoryService historyService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Override
    public ProcessDefinitionTree getProcessDefinitionTree() {
        List<Model> moduleList = repositoryService.createModelQuery().list();
        List<ProcessDefinitionTree> treeList = new ArrayList<>();
        for (Model model : moduleList) {
            ProcessDefinitionTree processDefinitionTree = new ProcessDefinitionTree();
            processDefinitionTree.setId(model.getId());
            processDefinitionTree.setTitle(model.getName() + "--" + "草稿");
            List<ProcessDefinitionTree> modelChildren = null;
            //获取由指定模型发布的所有流程，没有发布过模型部署id为空，则将children设置为null
            if (model.getDeploymentId() != null) {
                modelChildren = this.getModelChildren(model.getDeploymentId());
            }
            processDefinitionTree.setChildren(modelChildren);
            treeList.add(processDefinitionTree);
        }
        //增加一个顶级分类，否则前端删除时出错
        ProcessDefinitionTree parentTreeNode = new ProcessDefinitionTree();
        parentTreeNode.setId("1111-1111-1111");
        parentTreeNode.setTitle("所有模型");
        parentTreeNode.setChildren(treeList);
        return parentTreeNode;
    }
    
    /**
     * 获取由指定模型发布的所有流程
     * 
     * @param deploymentId
     * @return
     * @Description:由模型中的外键部署id来查找到最新的流程定义，因为由同一个模型发布的流程定义的key相同，然后使用key查出所有key相同的流程定义，即为指定模型的子项
     */
    private List<ProcessDefinitionTree> getModelChildren(String deploymentId) {
        //查找出指定模型发布的最新版本的流程定义
        ProcessDefinition processDefinition =
            repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        //根据流程定义的key查出所有由同一个模型发布的流程
        List<ProcessDefinition> list =
            repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinition.getKey()).list();
        List<ProcessDefinitionTree> treeChildren = new ArrayList<>();
        //封装为流程定义树实体
        for (ProcessDefinition wrapProcessDefinition : list) {
            ProcessDefinitionTree processDefinitionTree = new ProcessDefinitionTree();
            processDefinitionTree.setId(wrapProcessDefinition.getId());
            processDefinitionTree
                .setTitle(wrapProcessDefinition.getName() + "--版本" + wrapProcessDefinition.getVersion());
            treeChildren.add(processDefinitionTree);
        }
        return treeChildren;
    }
    
    @Override
    public void getMyWorkListBysearchText(PageQueryData<MyWorkEntity> pageQueryData) {
        switch (pageQueryData.getSearchText()) {
            case "HanglingWork":
                this.findHanglingWorkList(pageQueryData);
                break;
            case "FinishedWork":
                this.findFinishedWorkList(pageQueryData);
                break;
            case "PersonalDoneWork":
                this.findPersonalDoneWorkList(pageQueryData);
                break;
            default:
                break;
        }
    }
    
    /**
     * TODO 获取个人已办理的工作列表
     * 
     * @param pageQueryData
     * @Description:
     */
    private void findPersonalDoneWorkList(PageQueryData<MyWorkEntity> pageQueryData) {
        String realName = pageQueryData.getQueryId();
        List<MyWorkEntity> resultList = new ArrayList<MyWorkEntity>();
        // 根据当前人的ID查询
        List<HistoricTaskInstance> PersonalDoneTaskList =
            historyService.createHistoricTaskInstanceQuery().taskAssignee(realName).list();
        // 根据流程的业务ID查询实体并关联
        for (HistoricTaskInstance task : PersonalDoneTaskList) {
            //没有结束时间表示该环节尚未办理完结，则不应该出现在个人已办理列表中
            if (task.getEndTime() != null) {
                System.out.println(task.toString());
                //业务情况从流程实例历史表中获取
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
                //环节信息从历史task表中获取，因为历史表记录的信息比较完善
                HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .singleResult();
                MyWorkEntity myWorkEntity = new MyWorkEntity();
                myWorkEntity.setPersonalDoneWorkAttributes(historicProcessInstance, historicTaskInstance);
                resultList.add(myWorkEntity);
            }
        }
        pageQueryData.setQueryList(resultList);
    }
    
    /**
     * TODO 获取办结工作列表
     * 
     * @param pageQueryData
     * @Description:
     */
    private void findFinishedWorkList(PageQueryData<MyWorkEntity> pageQueryData) {
        String realName = pageQueryData.getQueryId();
        List<MyWorkEntity> resultList = new ArrayList<MyWorkEntity>();
        // 根据当前人的ID查询
        List<HistoricTaskInstance> PersonalDoneTaskList =
            historyService.createHistoricTaskInstanceQuery().taskAssignee(realName).list();
        // 根据流程的业务ID查询实体并关联
        for (HistoricTaskInstance task : PersonalDoneTaskList) {
            //某一个流程中，该用户办理的环节已经办理结束
            if (task.getEndTime() != null) {
                System.out.println(task.toString());
                //业务情况从流程实例历史表中获取
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
                //该流程存在结束时间，说明该流程实例已经完全结束
                if (historicProcessInstance.getEndTime() != null) {
                    MyWorkEntity myWorkEntity = new MyWorkEntity();
                    myWorkEntity.setFinishedWorkAttributes(historicProcessInstance);
                    resultList.add(myWorkEntity);
                }
            }
            pageQueryData.setQueryList(resultList);
        }
    }
    
    /**
     * 获取在办工作列表
     * 
     * @param pageQueryData
     * @Description:
     */
    private void findHanglingWorkList(PageQueryData<MyWorkEntity> pageQueryData) {
        String realName = pageQueryData.getQueryId();
        List<MyWorkEntity> resultList = new ArrayList<MyWorkEntity>();
        List<Task> tasks = new ArrayList<Task>();
        // 根据当前人的ID查询
        List<Task> todoList = taskService.createTaskQuery().taskAssignee(realName).list();
        // 根据当前人未签收的任务
        List<Task> unsignedTasks = taskService.createTaskQuery().taskCandidateUser(realName).list();
        // 合并
        tasks.addAll(todoList);
        tasks.addAll(unsignedTasks);
        // 根据流程的业务ID查询实体并关联
        for (Task task : tasks) {
            System.out.println(task.toString());
            //业务情况从流程实例历史表中获取
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
            //在办环节的开始时间、接办时间需要从历史task表中获取
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .singleResult();
            MyWorkEntity myWorkEntity = new MyWorkEntity();
            myWorkEntity.setHanglingWorkAttributes(historicProcessInstance, historicTaskInstance);
            resultList.add(myWorkEntity);
        }
        pageQueryData.setQueryList(resultList);
    }
}
