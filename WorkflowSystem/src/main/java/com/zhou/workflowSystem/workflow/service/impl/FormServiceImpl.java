package com.zhou.workflowSystem.workflow.service.impl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
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
        if (businessFormData.getId() == null) {
            formDao.save(businessFormData);
        } else {
            BusinessFormData EntityInDB = formDao.findById(businessFormData.getId()).orElse(null);
            BeanUtils.copyProperties(businessFormData, EntityInDB, this.getNullPropertyNames(businessFormData));
            formDao.save(EntityInDB);
        }
    }
    
    /**
     * @param source
     * @return 返回属性值为Null的属性名数组
     * @Description: 用于实体拷贝时忽略值为Null属性
     */
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
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
