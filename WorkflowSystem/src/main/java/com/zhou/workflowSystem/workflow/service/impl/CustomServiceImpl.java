package com.zhou.workflowSystem.workflow.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhou.workflowSystem.workflow.entity.ProcessDefinitionTree;
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
public class CustomServiceImpl implements ICustomService {
    //@Autowired
    //private CustomDao customDao;
    
    @Autowired
    private RepositoryService repositoryService;
    
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
}
