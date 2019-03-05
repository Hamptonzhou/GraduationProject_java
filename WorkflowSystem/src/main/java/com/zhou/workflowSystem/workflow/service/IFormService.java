package com.zhou.workflowSystem.workflow.service;

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
    void saveFormData(BusinessFormData businessFormData);
    
}
