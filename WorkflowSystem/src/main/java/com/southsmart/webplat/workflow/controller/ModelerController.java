package com.southsmart.webplat.workflow.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.southsmart.webplat.common.model.Result;
import com.southsmart.webplat.common.util.ResultUtil;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("model")
@RestController
@Slf4j
public class ModelerController {
    @Autowired
    ProcessEngine processEngine;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    ObjectMapper objectMapper;
    
    /**
     * 新建一个空模型
     * 
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("create")
    public void createModel(HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);
            Model modelData = repositoryService.newModel();
            
            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            
            //设置一些默认信息
            String name = "新建流程";
            String description = "";
            int revision = 1;
            String key = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName(name);
            modelData.setKey(key);
            
            //保存模型
            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
            response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());
        } catch (Exception e) {
            System.out.println("创建模型失败：");
        }
    }
    
    @RequestMapping("new")
    public Result newModel(HttpServletRequest request)
        throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        Model modelData = repositoryService.newModel();
        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        
        //设置一些默认信息
        String name = "new process";
        String description = "";
        int revision = 1;
        String key = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(name);
        modelData.setKey(key);
        
        //保存模型
        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
        Map<String, Object> map = new HashMap<>();
        map.put("modelId", modelData.getId());
        map.put("redirect", request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());
        return ResultUtil.success(map);
    }
    
    /**
     * 获取所有模型
     * 
     * @return
     */
    @RequestMapping("list")
    public List<Model> modelList() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<Model> models = repositoryService.createModelQuery().list();
        return models;
    }
    
    /**
     * 删除模型
     * 
     * @param id
     * @return
     */
    @RequestMapping("/delete/{id}")
    public Result deleteModel(@PathVariable("id") String id) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.deleteModel(id);
        return ResultUtil.success();
    }
    
    /**
     * 发布模型为流程定义
     * 
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("{id}/deployment")
    public Result deploy(@PathVariable("id") String id)
        throws Exception {
        //获取模型
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Model modelData = repositoryService.getModel(id);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
        
        if (bytes == null) {
            return ResultUtil.fail("模型数据为空，请先设计流程并成功保存，再进行发布。");
        }
        
        JsonNode modelNode = new ObjectMapper().readTree(bytes);
        
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        if (model.getProcesses().size() == 0) {
            return ResultUtil.fail("数据模型不符要求，请至少设计一条主线流程。");
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
        
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .deploymentId(deployment.getId())
            .latestVersion()
            .singleResult();
        String processDefinitionKey = processDefinition.getKey();
        return ResultUtil.success(processDefinitionKey);
    }
    
}
