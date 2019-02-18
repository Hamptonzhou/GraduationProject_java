package com.southsmart.webplat.workflow.controller;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.southsmart.webplat.common.model.Result;
import com.southsmart.webplat.common.util.ResultUtil;

@RequestMapping("model")
@RestController
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
    
    //##########################毕设开发，主要修改返回类型为Result##########################
    /**
     * 新建一个空模型
     * 
     * @param request
     * @return 返回封装的数据结构
     * @throws Exception
     * @Description:
     */
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
     * 获取模型图片
     * 
     * @param modelId
     * @return
     * @Description:
     */
    @RequestMapping("getImageByModelId")
    public Result getImageByModelId(HttpServletRequest request, String modelId) {
        Map<String, Object> results = new HashMap<>();
        byte[] image = repositoryService.getModelEditorSourceExtra(modelId);
        if (image != null) {
            results.put("image", Base64.getEncoder().encodeToString(image));
        }
        return ResultUtil.success(results);
    }
    
    /**
     * 返回编辑模型的页面地址
     * 
     * @param modelId
     * @return
     * @Description:
     */
    @RequestMapping("showModelEditPage")
    public Result showModelEditPage(HttpServletRequest request, String modelId) {
        Map<String, Object> map = new HashMap<>();
        map.put("redirect", request.getContextPath() + "/modeler.html?modelId=" + modelId);
        return ResultUtil.success(map);
    }
    
    /**
     * 发布模型为流程定义
     * 
     * @param ModelId
     * @return
     * @throws Exception
     */
    @RequestMapping("deployment")
    public Result deployment(String modelId)
        throws Exception {
        //获取模型
        Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
        if (bytes == null) {
            return ResultUtil.fail("模型数据为空，请先设计流程并成功保存，再进行发布。");
        }
        JsonNode modelNode = new ObjectMapper().readTree(bytes);
        
        //修改modelNode，默认设置流程定义的key，因为当key为空时，无法进行模型发布
        JsonNode processId = modelNode.get("properties").get("process_id");
        //初次发布时，流程标识为""，为其赋值，往后发布，必须使用第一次赋予的值，否则为不同的流程定义，而不是版本升序
        if ("\"\"".equals(processId.toString())) {
            ((ObjectNode)modelNode.get("properties")).put("process_id",
                "_" + UUID.randomUUID().toString().substring(0, 5));
            repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));
        }
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
        return ResultUtil.success(deployment.getId());
    }
    
    /**
     * 删除模型
     * 
     * @param id
     * @return
     */
    @RequestMapping("deleteModelById")
    public Result deleteModelById(String modelId) {
        Model model = repositoryService.createModelQuery().modelId(modelId).singleResult();
        if (model.getDeploymentId() == null) {
            repositoryService.deleteModel(modelId);
            return ResultUtil.success();
        } else {
            return ResultUtil.fail("模型下存在已发布的流程，请先删除流程！");
        }
    }
}
