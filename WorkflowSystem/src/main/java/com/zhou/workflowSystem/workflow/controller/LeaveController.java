package com.zhou.workflowSystem.workflow.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.workflowSystem.common.model.Result;
import com.zhou.workflowSystem.common.util.ParamUtils;
import com.zhou.workflowSystem.common.util.ResultUtil;
import com.zhou.workflowSystem.workflow.entity.Leave;
import com.zhou.workflowSystem.workflow.testservice.impl.LeaveService;

@RestController
@RequestMapping("leave")
public class LeaveController {
    
    @Autowired
    private LeaveService leaveService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @RequestMapping(value = "startProcess", method = RequestMethod.POST)
    public Result startProcess(HttpServletRequest request)
        throws Exception {
        Map<String, Object> paramMap = ParamUtils.getParamMap(request);
        String userId = ParamUtils.getStringParam("userId", paramMap);
        String processDefinitionKey = ParamUtils.getStringParam("processDefinitionKey", paramMap);
        Leave leave = ParamUtils.map2obj(paramMap, Leave.class);
        Map<String, Object> variables = new HashMap<String, Object>();
        ProcessInstance processInstance = leaveService.startWorkflow(leave, userId, variables, processDefinitionKey);
        System.out.println("流程已启动，流程ID：" + processInstance.getId());
        return ResultUtil.success(processInstance);
    }
    
    /**
     * 任务列表
     *
     * @param leave
     * @throws Exception
     */
    @RequestMapping(value = "list")
    public Result taskList(HttpServletRequest request)
        throws Exception {
        Map<String, Object> paramMap = ParamUtils.getParamMap(request);
        String userId = ParamUtils.getStringParam("userId", paramMap);
        String processDefinitionKey = ParamUtils.getStringParam("processDefinitionKey", paramMap);
        List<Leave> results = leaveService.findTodoTasks(userId, processDefinitionKey);
        return ResultUtil.success(results);
    }
    
    /**
     * 签收任务
     */
    @RequestMapping(value = "claim")
    public Result claim(String taskId, String userId) {
        //        String userId = "tijs";
        taskService.claim(taskId, userId);
        return ResultUtil.success();
    }
    
    /**
     * 任务列表
     *
     * @param leave
     */
    @RequestMapping(value = "view/{taskId}")
    public Result showTaskView(@PathVariable("taskId") String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance processInstance =
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        Leave leave = leaveService.get(processInstance.getBusinessKey());
        Map<String, Object> map = new HashMap<>();
        map.put("leave", leave);
        map.put("task", task);
        return ResultUtil.success(map);
    }
    
    /**
     * 完成任务
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "complete/{id}", method = {RequestMethod.POST, RequestMethod.GET})
    public Result complete(@PathVariable("id") String taskId) {
        Map<String, Object> variables = new HashMap<String, Object>();
        leaveService.complete(taskId, variables);
        return ResultUtil.success();
    }
    
    @RequestMapping(value = "approve", method = {RequestMethod.POST, RequestMethod.GET})
    public Result approve(String taskId, int result) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("agreement", result);
        leaveService.complete(taskId, variables);
        return ResultUtil.success();
    }
    
    @RequestMapping(value = "reapply", method = {RequestMethod.POST, RequestMethod.GET})
    public Result reapply(String taskId, int result) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("reapply", result);
        leaveService.complete(taskId, variables);
        return ResultUtil.success();
    }
    
    @RequestMapping(value = "get/{id}", method = {RequestMethod.POST, RequestMethod.GET})
    public Result getLeave(@PathVariable("id") String id) {
        Leave leave = leaveService.get(id);
        return ResultUtil.success(leave);
    }
    
    @RequestMapping(value = "getImage", method = {RequestMethod.POST, RequestMethod.GET})
    public Result getResource(String deploymentId)
        throws IOException {
        // 获取指定ID流程定义下的所有资源文件的名称列表
        List<String> names = repositoryService.getDeploymentResourceNames(deploymentId);
        String resourceName = null;
        String resourcexmlName = null;
        // 遍历资源文件名称列表
        for (String string : names) {
            // 获取'.png'结尾名称为流程图片名称
            if (string.endsWith(".png")) {
                resourceName = string;
            }
            // 获取'.xml'结尾名称为流程图片名称
            if (string.endsWith(".xml")) {
                resourcexmlName = string;
            }
        }
        // 如果流程图片存在
        if (resourceName != null) {
            InputStream in = repositoryService.getResourceAsStream(deploymentId, resourceName);
            InputStream xmlin = repositoryService.getResourceAsStream(deploymentId, resourcexmlName);
            // 指定拷贝目录
            File file = new File("/Downloads/" + resourceName);
            File xmlfile = new File("/Downloads/" + resourcexmlName);
            // 原始方式
            // OutputStream out = new FileOutputStream(file);
            // byte[] b = new byte[1024];
            // int len = 0;
            // while((len=in.read(b))!=-1) {
            // out.write(b, 0, len);
            // }
            // out.close();
            // 使用FileUtils文件操作工具类，将流程图片拷贝到指定目录下
            FileUtils.copyInputStreamToFile(in, file);
            FileUtils.copyInputStreamToFile(xmlin, xmlfile);
        }
        return ResultUtil.success();
    }
    
    @RequestMapping(value = "getHighlightImage", method = {RequestMethod.POST, RequestMethod.GET})
    public Result getHighlightImage(String processInstanceId)
        throws IOException {
        //        leaveService.getDueTaskHignlightImage(processInstanceId, "/Downloads/");
        leaveService.traceProcessImage(processInstanceId, "/Downloads/");
        return ResultUtil.success();
    }
    
}
