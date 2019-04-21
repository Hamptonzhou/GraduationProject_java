package com.zhou.workflowSystem.workflow.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.workflowSystem.common.model.Result;
import com.zhou.workflowSystem.common.util.PageQueryData;
import com.zhou.workflowSystem.common.util.ResultUtil;
import com.zhou.workflowSystem.workflow.entity.MyWorkEntity;
import com.zhou.workflowSystem.workflow.entity.ProcessDefinitionTree;
import com.zhou.workflowSystem.workflow.service.ICustomService;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月15日
 * @Version:1.1.0
 */
@RequestMapping("CustomConteoller")
@RestController
public class CustomConteoller {
    
    @Autowired
    private ICustomService<MyWorkEntity> customService;
    
    @Autowired
    private RepositoryService repositoryService;
    
    /**
     * 获取流程定义树
     * 
     * @return
     * @Description:
     */
    @RequestMapping("getProcessDefinitionTree")
    public Result getProcessDefinitionTree() {
        ProcessDefinitionTree treeList = customService.getProcessDefinitionTree();
        return ResultUtil.success(treeList);
    }
    
    /**
     * 获取流程定义图片
     * 
     * @param processDefinitionId
     * @return
     * @throws IOException
     * @Description:
     */
    @RequestMapping("getProcessDefinitionImage")
    public Result getProcessDefinitionImage(String processDefinitionId)
        throws IOException {
        Map<String, Object> result = new HashMap<>();
        InputStream in = repositoryService.getProcessDiagram(processDefinitionId);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int temp = 0;
        while (-1 != (temp = in.read(buffer))) {
            output.write(buffer, 0, temp);
        }
        String image = Base64.getEncoder().encodeToString(output.toByteArray());
        result.put("image", image);
        in.close();
        output.close();
        return ResultUtil.success(result);
    }
    
    /**
     * 删除已发布的模型(流程定义)
     * 
     * @param id
     * @return
     */
    @RequestMapping("deleteDeploymentProcessDefinitionById")
    public Result deleteDeploymentProcessDefinitionById(String processDefinitionId) {
        String deploymentId = repositoryService.getProcessDefinition(processDefinitionId).getDeploymentId();
        try {
            //删除指定版本的流程定义，如下代码导致直接删除部署，
            repositoryService.deleteDeployment(deploymentId);
        } catch (Exception e) {
            return ResultUtil.fail("删除失败！该流程由正在执行的任务，请联系管理员使用级联删除！");
        }
        return ResultUtil.success();
    }
    
    /**
     * 级联删除已发布的模型(流程定义)，级联删除流程下的所有任务、资源！
     * 
     * @param id
     * @return
     */
    @RequestMapping("cascadeDeleteDeployment")
    public Result cascadeDeleteDeployment(String processDefinitionId) {
        String deploymentId = repositoryService.getProcessDefinition(processDefinitionId).getDeploymentId();
        repositoryService.deleteDeployment(deploymentId, true);
        return ResultUtil.success("级联删除成功，该流程下的所有任务、资源、部署对象已彻底删除！");
    }
    
    //###############################我的工作模块接口################################################
    /**
     * 获取在办工作、个人已办、办结工作列表
     * 
     * @param pageQueryData 传递用户的真实姓名到queryId中，并且在searchText中指定查询数据的类型
     * @param request
     * @return
     * @throws Exception
     * @Description:searchText取值为：HanglingWork、FinishedWork、PersonalDoneWork
     */
    @RequestMapping(value = "getMyWorkListBySearchText")
    public Result getMyWorkListBySearchText(PageQueryData<MyWorkEntity> pageQueryData)
        throws Exception {
        customService.getMyWorkListBysearchText(pageQueryData);
        return ResultUtil.success(pageQueryData.getResult());
    }
    
    /**
     * 获取流程实例的状态图片，正在执行的环节会有红色边框
     * 
     * @param pageQueryData
     * @return
     * @throws Exception
     * @Description:queryId传递流程实例id
     */
    @RequestMapping(value = "getProcessImage")
    public Result getProcessImage(PageQueryData<MyWorkEntity> pageQueryData)
        throws Exception {
        customService.getProcessStatusImage(pageQueryData);
        return ResultUtil.success(pageQueryData.getSearchTextMap());
    }
    
    /**
     * 接办或退办任务
     * 
     * @param taskId
     * @param userId
     * @return
     * @Description:当userId为空时，执行退签功能。退签之后，组成员都可以查看任务内容。回退到组任务的前提是，本来是一个组任务
     */
    @RequestMapping(value = "claimTask")
    public Result claimTask(String taskId, String userId) {
        if (taskId == null) {
            return ResultUtil.fail("taskID不能为空 ");
        }
        customService.claimTask(taskId, userId);
        return ResultUtil.success();
    }
    
    /**
     * 完成任务
     * 
     * @param taskId
     * @return
     * @Description:queryId接收taskId
     */
    @RequestMapping(value = "completeTask", method = {RequestMethod.POST, RequestMethod.GET})
    public Result completeTask(String taskId) {
        if (taskId == null) {
            return ResultUtil.fail("taskID不能为空 ");
        }
        customService.completeTask(taskId);
        return ResultUtil.success();
    }
    
    /**
     * 设置备注内容
     * 
     * @param taskId
     * @param remarkContent
     * @return
     * @Description: 保存流程变量即可实现
     */
    @RequestMapping(value = "setRemarkContent", method = {RequestMethod.POST})
    public Result setRemarkContent(String taskId, String remarkContent) {
        if (taskId == null || remarkContent == null) {
            return ResultUtil.fail("taskID/remarkContent不能为空 ");
        }
        customService.setRemarkContent(taskId, remarkContent);
        return ResultUtil.success();
    }
    
    /**
     * 根据流程实例id获取流程实例的Business_Key
     * 
     * @param processInstanceId
     * @return
     * @Description:
     */
    @RequestMapping(value = "getBusinessFormId")
    public Result getBusinessFormId(String processInstanceId) {
        String businessFormId = customService.getBusinessFormId(processInstanceId);
        return ResultUtil.success(businessFormId);
    }
    
    /**
     * 根据流程定义Id,启动流程。同时将Business_Key字段与业务表单Id绑定
     * 
     * @param processDefinitionId
     * @return
     * @throws Exception
     * @Description:
     */
    @RequestMapping(value = "startProcessDefinition")
    public Result startProcessDefinition(Integer businessId)
        throws Exception {
        customService.startProcessDefinition(businessId);
        return ResultUtil.success();
    }
    
}
