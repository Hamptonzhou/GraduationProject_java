package com.zhou.workflowSystem.workflow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhou.workflowSystem.workflow.dao.FormDao;
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
@Service
public class FormServiceImpl implements IFormService {
    
    @Autowired
    private FormDao formDao;
    
    @Override
    public void saveOrUpdateFormData(BusinessFormData businessFormData) {
        formDao.save(businessFormData);
    }
    
    @Override
    public BusinessFormData getFormDataById(Integer formDataId) {
        BusinessFormData formData = null;
        if (formDataId != null) {
            formData = formDao.findById(formDataId).orElse(null);
        }
        return formData;
    }
    
    @Override
    public List<BusinessFormData> getAllFormData() {
        return formDao.findAll();
    }
    
}
