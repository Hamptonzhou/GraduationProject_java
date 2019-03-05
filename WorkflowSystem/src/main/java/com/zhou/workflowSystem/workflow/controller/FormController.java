package com.zhou.workflowSystem.workflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.workflowSystem.common.model.Result;
import com.zhou.workflowSystem.common.util.ResultUtil;
import com.zhou.workflowSystem.workflow.entity.BusinessFormData;
import com.zhou.workflowSystem.workflow.service.IFormService;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年3月5日
 * @Version:1.1.0
 */
@RequestMapping("FormController")
@RestController
public class FormController {
    
    @Autowired
    private IFormService formService;
    
    /**
     * 保存业务表单
     * 
     * @return
     * @Description:
     */
    @RequestMapping("saveFormData")
    public Result saveFormData(BusinessFormData businessFormData) {
        formService.saveFormData(businessFormData);
        return ResultUtil.success();
    }
}
