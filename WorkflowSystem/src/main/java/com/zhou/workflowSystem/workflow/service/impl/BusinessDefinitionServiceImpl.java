package com.zhou.workflowSystem.workflow.service.impl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zhou.utils.webservice.WebServiceUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhou.utils.PageQueryData;
import com.zhou.workflowSystem.workflow.dao.BusinessDefinitionDao;
import com.zhou.workflowSystem.workflow.entity.BusinessDefinition;
import com.zhou.workflowSystem.workflow.service.IBusinessDefinitionService;

import javax.servlet.http.HttpServletRequest;

/**
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年3月7日
 * @Version:1.1.0
 */
@Service
public class BusinessDefinitionServiceImpl implements IBusinessDefinitionService {

    @Autowired
    private BusinessDefinitionDao businessDefinitionDao;

    @Override
    public void getBusinessDefinitionList(PageQueryData<BusinessDefinition> pageQueryData) {
        String userId = pageQueryData.getQueryId();
        List<BusinessDefinition> list =  businessDefinitionDao.findByCreatorIdAndStarted(userId,false);
        pageQueryData.setTotal(list.size());
        pageQueryData.setQueryList(list);
    }

    @Override
    public void saveOrUpdateBusinessDefinition(BusinessDefinition businessDefinition) {
        if (businessDefinition != null) {
            businessDefinitionDao.save(businessDefinition);
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
    public void deleteBusinessDefinitionByIds(Integer[] ids) {
        if (ids != null) {
            for (Integer id : ids) {
                BusinessDefinition businessDefinition = businessDefinitionDao.findById(id).orElse(null);
                if (businessDefinition != null) {
                    businessDefinitionDao.deleteById(id);
                }
            }
        }
    }

    @Override
    public void setBusinessDefRemark(Integer businessId, String remarkContent) {
        BusinessDefinition businessDefinition = businessDefinitionDao.findById(businessId).orElse(null);
        if (businessDefinition != null) {
            businessDefinition.setRemarkContent(remarkContent);
            businessDefinitionDao.save(businessDefinition);
        }
    }
}
