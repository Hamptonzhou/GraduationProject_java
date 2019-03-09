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
    @RequestMapping("saveOrUpdateFormData")
    public Result saveOrUpdateFormData(BusinessFormData businessFormData) {
        formService.saveOrUpdateFormData(businessFormData);
        return ResultUtil.success();
    }
    
    /**
     * 获取表单实体对象
     * 
     * @param FormDataId
     * @return
     * @Description:
     */
    @RequestMapping("getFormDataById")
    public Result getFormDataById(Integer formDataId) {
        BusinessFormData formData = formService.getFormDataById(formDataId);
        if (formData != null) {
            return ResultUtil.success(formData);
        } else {
            return ResultUtil.fail("表单不存在！");
        }
    }
    
    /**
     * 获取所有表单实体对象
     * 
     * @return
     * @Description:
     */
    @RequestMapping("getAllFormData")
    public Result getAllFormData() {
        return ResultUtil.success(formService.getAllFormData());
    }
}
