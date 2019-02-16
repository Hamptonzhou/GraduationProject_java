package com.southsmart.webplat.workflow.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.southsmart.webplat.common.model.Result;
import com.southsmart.webplat.common.util.ResultUtil;
import com.southsmart.webplat.workflow.entity.ProcessDefinitionTree;
import com.southsmart.webplat.workflow.service.ICustomService;

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
    private ICustomService customService;
    
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
}
