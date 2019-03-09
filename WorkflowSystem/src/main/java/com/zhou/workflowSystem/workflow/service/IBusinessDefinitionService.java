package com.zhou.workflowSystem.workflow.service;

import com.zhou.utils.PageQueryData;
import com.zhou.workflowSystem.workflow.entity.BusinessDefinition;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年3月7日
 * @Version:1.1.0
 */
public interface IBusinessDefinitionService {
    
    /**
     * 获取记录列表
     * 
     * @param pageQueryData
     * @Description:
     */
    void getBusinessDefinitionList(PageQueryData<BusinessDefinition> pageQueryData);
    
    /**
     * 新增或修改记录
     * 
     * @param businessDefinition
     * @Description:
     */
    void saveOrUpdateBusinessDefinition(BusinessDefinition businessDefinition);
    
    /**
     * 删除一条或多条记录
     * 
     * @param ids
     * @Description:
     */
    void deleteBusinessDefinitionByIds(Integer[] ids);
    
}
