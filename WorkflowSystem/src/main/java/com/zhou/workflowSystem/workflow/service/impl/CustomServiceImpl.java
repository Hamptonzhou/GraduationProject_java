package com.zhou.workflowSystem.workflow.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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
     * 获取个人已办理的工作列表
     * 
     * @param pageQueryData
     * @Description:
     */
    private void findPersonalDoneWorkList(PageQueryData<MyWorkEntity> pageQueryData) {
        String realName = pageQueryData.getQueryId();
        List<MyWorkEntity> MyWorkEntityResultList = new ArrayList<MyWorkEntity>();
        MyWorkEntity myWorkEntity = new MyWorkEntity();
        myWorkEntity.setBusinessName("个人已办理的工作列表");
        MyWorkEntityResultList.add(myWorkEntity);
        pageQueryData.setQueryList(MyWorkEntityResultList);
        
    }
    
    /**
     * 获取办结工作列表
     * 
     * @param pageQueryData
     * @Description:
     */
    private void findFinishedWorkList(PageQueryData<MyWorkEntity> pageQueryData) {
        String realName = pageQueryData.getQueryId();
        List<MyWorkEntity> MyWorkEntityResultList = new ArrayList<MyWorkEntity>();
        MyWorkEntity myWorkEntity = new MyWorkEntity();
        myWorkEntity.setBusinessName("办结工作列表");
        MyWorkEntityResultList.add(myWorkEntity);
        pageQueryData.setQueryList(MyWorkEntityResultList);
        
    }
    
    /**
     * 获取在办工作列表
     * 
     * @param pageQueryData
     * @Description:
     */
    private void findHanglingWorkList(PageQueryData<MyWorkEntity> pageQueryData) {
        String realName = pageQueryData.getQueryId();
        List<MyWorkEntity> MyWorkEntityResultList = new ArrayList<MyWorkEntity>();
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
            String processInstanceId = task.getProcessInstanceId();
            System.out.println(task.toString());
            ProcessInstance processInstance =
                runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            //String businessKey = processInstance.getBusinessKey();
            MyWorkEntity myWorkEntity = new MyWorkEntity();
            myWorkEntity.setProcessInstance(new CustomProcessInstance(processInstance));
            myWorkEntity.setTask(new CustomActivitiTask(task));
            myWorkEntity.setBusinessName(processInstance.getProcessDefinitionName());
            MyWorkEntityResultList.add(myWorkEntity);
        }
        pageQueryData.setQueryList(MyWorkEntityResultList);
    }
}
