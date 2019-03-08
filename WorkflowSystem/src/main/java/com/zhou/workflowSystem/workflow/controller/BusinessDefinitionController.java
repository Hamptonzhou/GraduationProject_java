package com.zhou.workflowSystem.workflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.utils.PageQueryData;
import com.zhou.workflowSystem.common.model.Result;
import com.zhou.workflowSystem.common.util.ResultUtil;
import com.zhou.workflowSystem.workflow.entity.BusinessDefinition;
import com.zhou.workflowSystem.workflow.service.IBusinessDefinitionService;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年3月7日
 * @Version:1.1.0
 */
@RestController
@RequestMapping("BusinessDefinitionController")
public class BusinessDefinitionController {
    
    @Autowired
    private IBusinessDefinitionService businessDefinitionService;
    
    /**
     * 获取业务定义列表
     * 
     * @return
     * @Description:
     */
    @RequestMapping("getBusinessDefinitionList")
    public Result getBusinessDefinitionList(PageQueryData<BusinessDefinition> pageQueryData) {
        businessDefinitionService.getBusinessDefinitionList(pageQueryData);
        return ResultUtil.success();
    }
    
    /**
     * 新增或修改记录
     * 
     * @param material
     * @return
     * @Description:
     */
    @RequestMapping("saveOrUpdateBusinessDefinition")
    public Result saveOrUpdateBusinessDefinition(BusinessDefinition businessDefinition) {
        businessDefinitionService.saveOrUpdateBusinessDefinition(businessDefinition);
        return ResultUtil.success();
    }
    
    /**
     * 删除一条或多条记录
     * 
     * @param ids
     * @return
     * @Description:
     */
    @RequestMapping("deleteBusinessDefinitionByIds")
    public Result deleteBusinessDefinitionByIds(Integer[] ids) {
        businessDefinitionService.deleteMaterialByIds(ids);
        return ResultUtil.success();
    }
    
}