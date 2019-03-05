package com.zhou.workflowSystem.workflow.service.impl;

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
    public void saveFormData(BusinessFormData businessFormData) {
        formDao.save(businessFormData);
    }
    
}
