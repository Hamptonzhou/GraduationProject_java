package com.zhou.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.zhou.dao.IDepartmentDao;
import com.zhou.entity.DepartmentInfo;
import com.zhou.service.IDepartmentService;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月17日
 * @Version:1.1.0
 */
@Service
public class DepartmentServiceImpl implements IDepartmentService {
    
    @Autowired
    private IDepartmentDao departmentDao;
    
    @Override
    public List<DepartmentInfo> getDepartmentTree() {
        List<DepartmentInfo> list = departmentDao.findAll();
        List<DepartmentInfo> treeList = new ArrayList<>();
        for (DepartmentInfo departmentInfo : list) {
            if ("#".equals(departmentInfo.getParentId())) {
                List<DepartmentInfo> children = departmentDao.findByParentId(departmentInfo.getId());
                departmentInfo.setChildren(children);
                treeList.add(departmentInfo);
            }
        }
        return treeList;
    }
    
    @Override
    public void saveOrUpdateDepartment(DepartmentInfo departmentInfo) {
        departmentDao.save(departmentInfo);
    }
    
    @Override
    public void deleteDepartment(String id) {
        if (departmentDao.findById(id).orElse(null) != null) {
            departmentDao.deleteById(id);
        }
    }
    
    @Override
    public List<DepartmentInfo> getDepartmentList(String parentId) {
        return departmentDao.findByParentId(parentId);
    }
}
