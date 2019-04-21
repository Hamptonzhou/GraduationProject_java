package com.zhou.workflowSystem.workflow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhou.workflowSystem.common.util.PageQueryData;
import com.zhou.workflowSystem.workflow.dao.BusinessDefinitionDao;
import com.zhou.workflowSystem.workflow.entity.BusinessDefinition;
import com.zhou.workflowSystem.workflow.service.IBusinessDefinitionService;

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
