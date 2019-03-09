package com.zhou.workflowSystem.workflow.service;

import java.util.List;

import com.zhou.workflowSystem.workflow.entity.BusinessFormData;

/**
 * 操作表单实体接口
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年3月5日
 * @Version:1.1.0
 */
public interface IFormService {
    
    /**
     * 保存表单的json数据
     * 
     * @param formData
     * @Description:
     */
    void saveOrUpdateFormData(BusinessFormData businessFormData);
    
    /**
     * 根据表单id获取表单实体对象
     * 
     * @param formDataId
     * @return
     * @Description:
     */
    BusinessFormData getFormDataById(Integer formDataId);
    
    /**
     * 获取所有表单数据
     * 
     * @return
     * @Description:
     */
    List<BusinessFormData> getAllFormData();
    
}
